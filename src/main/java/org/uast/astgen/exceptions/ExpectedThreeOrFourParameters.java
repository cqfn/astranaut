/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected 3 or 4 parameters".
 *
 * @since 1.0
 */
public final class ExpectedThreeOrFourParameters extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new ExpectedThreeOrFourParameters();

    /**
     * Constructor.
     */
    private ExpectedThreeOrFourParameters() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Expected 3 or 4 parameters";
    }
}
