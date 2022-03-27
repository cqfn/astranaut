/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected identifier without tag, parameters and data".
 *
 * @since 1.0
 */
public final class ExpectedSimpleIdentifier extends ParserException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text
     */
    public ExpectedSimpleIdentifier(final String text) {
        super();
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected identifier without tag, parameters and data: '")
            .append(this.text)
            .append('\'')
            .toString();
    }
}
