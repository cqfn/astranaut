/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Unknown symbol".
 *
 * @since 1.0
 */
public class UnknownSymbol extends ParserException {
    /**
     * The symbol.
     */
    private final char symbol;

    /**
     * Constructor.
     * @param symbol The symbol
     */
    public UnknownSymbol(final char symbol) {
        super();
        this.symbol = symbol;
    }

    @Override
    public final String getErrorMessage() {
        return new StringBuilder()
            .append("Unknown symbol: \'")
            .append(this.symbol)
            .append('\'')
            .toString();
    }
}
