/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected a native code literal".
 *
 * @since 1.0
 */
public final class ExpectedNativeLiteral extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new ExpectedNativeLiteral();

    /**
     * Constructor.
     */
    private ExpectedNativeLiteral() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Expected a native code literal: '$code$'";
    }
}
