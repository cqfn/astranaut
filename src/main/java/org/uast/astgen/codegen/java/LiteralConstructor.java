/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Literal;

/**
 * Constructs classes, fields and methods for rules that describe literals.
 *
 * @since 1.0
 */
abstract class LiteralConstructor extends BaseConstructor {
    /**
     * The rule.
     */
    private final Literal rule;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    LiteralConstructor(final Environment env, final Literal rule, final Klass klass) {
        super(env, klass);
        this.rule = rule;
    }

    /**
     * Returns the rule.
     * @return The rule
     */
    protected Literal getRule() {
        return this.rule;
    }

    @Override
    protected String getType() {
        return this.rule.getType();
    }
}
