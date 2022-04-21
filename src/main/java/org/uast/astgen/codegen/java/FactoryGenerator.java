/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import org.uast.astgen.rules.Literal;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Statement;

/**
 * Generates a factory for node creation.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public final class FactoryGenerator extends BaseGenerator {
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
