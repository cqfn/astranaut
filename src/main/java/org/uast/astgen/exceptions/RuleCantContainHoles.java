/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "This rule cannot contain holes".
 *
 * @since 1.0
 */
public final class RuleCantContainHoles extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new RuleCantContainHoles();

    /**
     * Constructor.
     */
    private RuleCantContainHoles() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "This rule cannot contain holes";
    }
}
