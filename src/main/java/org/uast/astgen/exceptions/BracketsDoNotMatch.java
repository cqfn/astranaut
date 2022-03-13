/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.exceptions;

/**
 * Exception "Opening and closing brackets do not match".
 *
 * @since 1.0
 */
public class BracketsDoNotMatch extends ParserException {
    /**
     * Opening bracket.
     */
    private final char opening;

    /**
     * Closing bracket.
     */
    private final char closing;

    /**
     * Constructor.
     * @param opening Opening bracket
     * @param closing Closing bracket
     */
    public BracketsDoNotMatch(final char opening, final char closing) {
        this.opening = opening;
        this.closing = closing;
    }

    @Override
    public final String getErrorMessage() {
        return new StringBuilder()
            .append("Opening \'")
            .append(this.opening)
            .append("\' and closing \'")
            .append(this.closing)
            .append("\' brackets does not match")
            .toString();
    }
}
