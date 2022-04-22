/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Map;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Statement;

/**
 * Generates source code for rules that describe nodes.
 *
 * @since 1.0
 */
public class NodeGenerator {
    /**
     * The prepared environment collection required for generation,
     * one object for each programming language.
     */
    private final Map<String, Environment> envs;

    /**
     * Constructor.
     * @param envs The prepared environment collection required for generation
     */
    public NodeGenerator(final Map<String, Environment> envs) {
        this.envs = envs;
    }

    /**
     * Generates source code for node descriptor.
     * @param stmt The statement that contains node descriptor.
     * @return Compilation unit
     */
    public CompilationUnit generate(final Statement<Node> stmt) {
        final String language = stmt.getLanguage();
        final Node rule = stmt.getRule();
        final CompilationUnit unit;
        if (rule.isOrdinary()) {
            unit = new OrdinaryNodeGenerator(this.envs.get(language), stmt).generate();
        } else if (rule.isAbstract()) {
            unit = new AbstractNodeGenerator(this.envs.get(language), stmt).generate();
        } else if (rule.isList()) {
            unit = new ListNodeGenerator(this.envs.get(language), stmt).generate();
        } else {
            throw new IllegalStateException();
        }
        return unit;
    }
}
