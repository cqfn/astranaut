/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected unique numbers of holes".
 *
 * @since 1.0
 */
public final class ExpectedUniqueNumbers extends ParserException {
    /**
     * The duplicated hole number.
     */
    private final int number;

    /**
     * Constructor.
     * @param number A duplicated hole number
     */
    public ExpectedUniqueNumbers(final int number) {
        super();
        this.number = number;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected unique numbers of holes. Duplicated: '#")
            .append(this.number)
            .append('\'')
            .toString();
    }
}
