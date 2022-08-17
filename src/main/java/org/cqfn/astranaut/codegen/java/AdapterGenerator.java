/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.codegen.java;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Generates an adapter for syntax tree transformation.
 *
 * @since 0.1.5
 */
public final class AdapterGenerator extends BaseGenerator {
    /**
     * The 'Adapter' string.
     */
    private static final String STR_ADAPTER = "Adapter";

    /**
     * The 'String' string.
     */
    private static final String STR_STRING = "String";

    /**
     * Language for which the factory is generated.
     */
    private final String language;

    /**
     * The number of rules.
     */
    private final int count;

    /**
     * The class.
     */
    private Klass klass;

    /**
     * Language for which the factory is generated (from capital letter).
     */
    private String clang;

    /**
     * The class name.
     */
    private String classname;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param language Language for which the factory is generated
     * @param count The count of rules
     */
    AdapterGenerator(final Environment env, final String language, final int count) {
        super(env);
        this.language = language;
        this.count = count;
    }

    /**
     * Return the name of the generated class.
     * @return The class name
     */
    public String getClassname() {
        return this.classname;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        this.createClass();
        this.createConstructor();
        this.createInitializer();
        this.createRulesNumberField();
        this.createRulePrefixField();
        this.createRuleInstanceNameField();
        final String pkg = this.getPackageName(this.language);
        final CompilationUnit unit = new CompilationUnit(
            env.getLicense(),
            pkg,
            this.klass
        );
        this.addImports(unit);
        return unit;
    }

    /**
     * Creates the class constructor.
     */
    private void createClass() {
        assert !this.language.isEmpty();
        this.clang = this.language.substring(0, 1)
            .toUpperCase(Locale.ENGLISH)
            .concat(this.language.substring(1));
        final String brief = String.format(
            "Adapter that converts syntax trees, prepared by the parser of the %s language",
            this.clang
        );
        this.classname = String.format("%sAdapter", this.clang);
        this.klass = new Klass(brief, this.classname);
        this.klass.makeFinal();
        this.klass.setParentClass(AdapterGenerator.STR_ADAPTER);
    }

    /**
     * Creates the constructor and the static instance.
     */
    private void createConstructor() {
        final Field field = new Field(
            "The instance",
            AdapterGenerator.STR_ADAPTER,
            "INSTANCE"
        );
        field.makePublic();
        field.makeStaticFinal();
        field.setInitExpr(String.format("new %s()", this.classname));
        this.klass.addField(field);
        final Constructor ctor = new Constructor(this.classname);
        ctor.makePrivate();
        ctor.setCode(
            String.format(
                "super(Collections.unmodifiableList(%s.init()), %sFactory.INSTANCE);",
                this.classname,
                this.clang
            )
        );
        this.klass.addConstructor(ctor);
    }

    /**
     * Creates the 'init()' method.
     */
    private void createInitializer() {
        final Method method = new Method(
            "Initializes the list of node converters",
            "init"
        );
        method.makePrivate();
        method.makeStatic();
        method.setReturnType(
            "List<Converter>",
            "The list of node converters"
        );
        final List<String> code = Arrays.asList(
            String.format(
                "final List<Converter> rules = new ArrayList<>(%s.RULES_NUM);",
                this.classname
            ),
            String.format(
                "for (int index = 0; index < %s.RULES_NUM; index = index + 1) {",
                this.classname
            ),
            String.format(
                "    final String name = %s.PREFIX",
                this.classname
            ),
            "\t.concat(Integer.toString(index));",
            "    try {",
            "        final Class<?> cls = Class.forName(name);",
            String.format(
                "        final Field instance = cls.getField(%s.RULE_INSTANCE);",
                this.classname
            ),
            "        rules.add((Converter) instance.get(null));",
            "    } catch (final ClassNotFoundException | IllegalAccessException",
            "\t| NoSuchFieldException ignored) {",
            "        continue;",
            "    }",
            "}",
            "return rules;"
        );
        method.setCode(String.join("\n", code));
        this.klass.addMethod(method);
    }

    /**
     * Adds imports to compilation unit.
     * @param unit Compilation unit
     */
    private void addImports(final CompilationUnit unit) {
        unit.addImport("java.lang.reflect.Field");
        unit.addImport("java.util.ArrayList");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        final Environment env = this.getEnv();
        final String base = env.getBasePackage();
        unit.addImport(base.concat(".Adapter"));
        unit.addImport(base.concat(".Converter"));
    }

    /**
     * Creates field that contains a number of rules.
     */
    private void createRulesNumberField() {
        final Field field = new Field(
            "The number of rules",
            "int",
            "RULES_NUM"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(Integer.toString(this.count));
        this.klass.addField(field);
    }

    /**
     * Creates field that contains a prefix of a rule class name.
     */
    private void createRulePrefixField() {
        final Field field = new Field(
            "The prefix of a rule class name",
            AdapterGenerator.STR_STRING,
            "PREFIX"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(
            String.format("\"%s\"", this.getPackageName(this.language).concat(".rules.Rule"))
        );
        this.klass.addField(field);
    }

    /**
     * Creates field that contains a name of the rule instance field.
     */
    private void createRuleInstanceNameField() {
        final Field field = new Field(
            "The name of the rule instance field",
            AdapterGenerator.STR_STRING,
            "RULE_INSTANCE"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr("\"INSTANCE\"");
        this.klass.addField(field);
    }
}
