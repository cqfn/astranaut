/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Unexpected hole number".
 *
 * @since 1.0
 */
public final class UnexpectedNumberUsed extends ParserException {
    /**
     * The unexpected hole number.
     */
    private final int number;

    /**
     * Constructor.
     * @param number An unexpected hole number
     */
    public UnexpectedNumberUsed(final int number) {
        super();
        this.number = number;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Unexpected hole number, was not used in the left part: '#")
            .append(this.number)
            .append('\'')
            .toString();
    }
}
