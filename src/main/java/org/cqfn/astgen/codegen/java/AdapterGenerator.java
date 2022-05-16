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
package org.cqfn.astgen.codegen.java;

import java.util.Locale;

/**
 * Generates an adapter for syntax tree transformation.
 *
 * @since 1.0
 */
public final class AdapterGenerator extends BaseGenerator {
    /**
     * The 'Adapter' string.
     */
    private static final String STR_ADAPTER = "Adapter";

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
        final String pkg = this.getPackageName(this.language);
        final CompilationUnit unit = new CompilationUnit(
            env.getLicense(),
            pkg,
            this.klass
        );
        this.addImports(unit, pkg);
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
        final StringBuilder code = new StringBuilder(256);
        code.append("return Arrays.asList(\n");
        boolean flag = false;
        for (int index = 0; index < count; index = index + 1) {
            if (flag) {
                code.append(",\n");
            }
            flag = true;
            code.append("\tRule").append(index).append(".INSTANCE");
        }
        code.append("\n);\n");
        method.setCode(code.toString());
        this.klass.addMethod(method);
    }

    /**
     * Adds imports to compilation unit.
     * @param unit Compilation unit
     * @param pkg Package name
     */
    private void addImports(final CompilationUnit unit, final String pkg) {
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        final Environment env = this.getEnv();
        final String base = env.getBasePackage();
        unit.addImport(base.concat(".Adapter"));
        unit.addImport(base.concat(".Converter"));
        for (int index = 0; index < this.count; index = index + 1) {
            unit.addImport(
                String.format(
                    "%s.rules.Rule%d",
                    pkg,
                    index
                )
            );
        }
    }
}
