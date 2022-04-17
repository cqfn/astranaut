/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;

/**
 * Generates source code for rules that describe nodes.
 *
 * @since 1.0
 */
public abstract class BaseNodeGenerator extends BaseGenerator {
    /**
     * Constructor.
     * @param env The environment required for generation.
     */
    BaseNodeGenerator(final Environment env) {
        super(env);
    }

    /**
     * Creates class for node construction.
     * @param rule The rule
     * @return The class constructor
     */
    protected static Klass createClass(final Node rule) {
        final String type = rule.getType();
        final Klass klass = new Klass(
            String.format("Node that describes the '%s' type", type),
            type
        );
        klass.makeFinal();
        klass.setInterfaces("Node");
        return klass;
    }
}
