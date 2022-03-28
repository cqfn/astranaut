/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.exceptions;

/**
 * Exception that informs that bracket is not closed.
 *
 * @since 1.0
 */
public class NotClosedBracket extends ParserException {
    /**
     * Opening bracket.
     */
    private final char opening;

    /**
     * Constructor.
     * @param opening Opening bracket
     */
    public NotClosedBracket(final char opening) {
        this.opening = opening;
    }

    @Override
    public final String getErrorMessage() {
        return new StringBuilder()
            .append("Bracket is not closed: \'")
            .append(this.opening)
            .append('\'')
            .toString();
    }
}
