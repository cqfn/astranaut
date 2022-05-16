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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import org.cqfn.astgen.rules.Literal;
import org.cqfn.astgen.rules.Node;
import org.cqfn.astgen.rules.Program;
import org.cqfn.astgen.rules.Statement;

/**
 * Generates a factory for node creation.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public final class FactoryGenerator extends BaseGenerator {
    /**
     * The 'factory' string.
     */
    private static final String STR_FACTORY = "Factory";

    /**
     * The program.
     */
    private final Program program;

    /**
     * Language for which the factory is generated.
     */
    private final String language;

    /**
     * Common ("green") nodes set.
     */
    private Set<String> common;

    /**
     * Specific nodes set.
     */
    private Set<String> specific;

    /**
     * The class.
     */
    private Klass klass;

    /**
     * The class name.
     */
    private String classname;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param program The program
     * @param language Language for which the factory is generated
     */
    FactoryGenerator(final Environment env, final Program program, final String language) {
        super(env);
        this.program = program;
        this.language = language;
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
        this.prepareRuleSet();
        this.createClass();
        this.createConstructor();
        this.createInitializer();
        final CompilationUnit unit = new CompilationUnit(
            env.getLicense(),
            this.getPackageName(this.language),
            this.klass
        );
        this.addImports(unit);
        return unit;
    }

    /**
     * Prepares a set of nodes for generation.
     */
    private void prepareRuleSet() {
        this.common = new TreeSet<>();
        this.specific = new TreeSet<>();
        this.prepareNodeSet();
        this.prepareLiteralSet();
    }

    /**
     * Prepares set of nodes.
     */
    private void prepareNodeSet() {
        for (final Statement<Node> statement : this.program.getNodes()) {
            final Node rule = statement.getRule();
            final String lang = statement.getLanguage();
            if (rule.isOrdinary() || rule.isList()) {
                if (lang.equals(this.language)) {
                    this.specific.add(rule.getType());
                } else if (lang.isEmpty() && !this.language.isEmpty()) {
                    this.common.add(rule.getType());
                }
            }
        }
    }

    /**
     * Prepares set of literals.
     */
    private void prepareLiteralSet() {
        for (final Statement<Literal> statement : this.program.getLiterals()) {
            final Literal rule = statement.getRule();
            final String lang = statement.getLanguage();
            if (lang.equals(this.language)) {
                this.specific.add(rule.getType());
            } else if (lang.isEmpty() && !this.language.isEmpty()) {
                this.common.add(rule.getType());
            }
        }
    }

    /**
     * Creates the class constructor.
     */
    private void createClass() {
        final String name;
        if (this.language.isEmpty()) {
            name = "green";
        } else {
            name = this.language;
        }
        final String brief = String.format("Factory that creates '%s' nodes", name);
        this.classname = String.format(
            "%s%sFactory",
            name.substring(0, 1).toUpperCase(Locale.ENGLISH),
            name.substring(1)
        );
        this.klass = new Klass(brief, this.classname);
        this.klass.makeFinal();
        this.klass.setParentClass(FactoryGenerator.STR_FACTORY);
    }

    /**
     * Creates the constructor and the static instance.
     */
    private void createConstructor() {
        final Field field = new Field(
            "The instance",
            FactoryGenerator.STR_FACTORY,
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
                "super(Collections.unmodifiableMap(%s.init()));",
                this.classname
            )
        );
        this.klass.addConstructor(ctor);
    }

    /**
     * Creates the 'init()' method.
     */
    private void createInitializer() {
        final Method method = new Method(
            "Initialises the set of types arranged by name",
            "init"
        );
        method.makePrivate();
        method.makeStatic();
        method.setReturnType(
            "Map<String, Type>",
            "The map of types by name"
        );
        final List<String> code = Arrays.asList(
            "final List<Type> types = Arrays.asList(",
            this.createList(),
            ");",
            "final Map<String, Type> map = new TreeMap<>();",
            "for (final Type type : types) {",
            "    map.put(type.getName(), type);",
            "}\n",
            "return map;"
        );
        method.setCode(String.join("\n", code));
        this.klass.addMethod(method);
    }

    /**
     * Creates list of classes (nodes and literals).
     * @return The string where all classes are enumerated
     */
    private String createList() {
        final Set<String> set = new TreeSet<>();
        set.addAll(this.common);
        set.addAll(this.specific);
        boolean flag = false;
        final StringBuilder result = new StringBuilder();
        for (final String name : set) {
            if (flag) {
                result.append(",\n");
            }
            flag = true;
            result.append('\t').append(name).append(".TYPE");
        }
        return result.toString();
    }

    /**
     * Adds imports to compilation unit.
     * @param unit Compilation unit
     */
    private void addImports(final CompilationUnit unit) {
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        unit.addImport("java.util.TreeMap");
        final Environment env = this.getEnv();
        final String base = env.getBasePackage();
        unit.addImport(base.concat(".Factory"));
        unit.addImport(base.concat(".Type"));
        for (final String name : this.common) {
            unit.addImport(String.format("%s.green.%s", env.getRootPackage(), name));
        }
    }
}
