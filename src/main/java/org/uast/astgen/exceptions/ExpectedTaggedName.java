/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected (tagged) name without parameters and data".
 *
 * @since 1.0
 */
public final class ExpectedTaggedName extends ParserException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text
     */
    public ExpectedTaggedName(final String text) {
        super();
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected (tagged) name without parameters and data: '")
            .append(this.text)
            .append('\'')
            .toString();
    }
}
