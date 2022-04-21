/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

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

    @Override
    public CompilationUnit generate() {
        this.prepareRuleSet();
        return null;
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
}
