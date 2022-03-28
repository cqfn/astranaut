/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected a number after '#' symbol".
 *
 * @since 1.0
 */
public final class ExpectedNumber extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new ExpectedNumber();

    /**
     * Constructor.
     */
    private ExpectedNumber() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Expected a number after '#' symbol";
    }
}
