/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected an identifier after '@' symbol".
 *
 * @since 1.0
 */
public final class ExpectedIdentifierAfterAt extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new ExpectedIdentifierAfterAt();

    /**
     * Constructor.
     */
    private ExpectedIdentifierAfterAt() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Expected an identifier after '@' symbol";
    }
}
