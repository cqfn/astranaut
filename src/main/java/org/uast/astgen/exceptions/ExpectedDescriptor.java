/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected a descriptor".
 *
 * @since 1.0
 */
public final class ExpectedDescriptor extends ParserException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text
     */
    public ExpectedDescriptor(final String text) {
        super();
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected a descriptor: '")
            .append(this.text)
            .append('\'')
            .toString();
    }
}
