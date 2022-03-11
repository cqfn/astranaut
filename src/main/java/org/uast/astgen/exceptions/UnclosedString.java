/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Unclosed string".
 *
 * @since 1.0
 */
public class UnclosedString extends ParserException {
    /**
     * The content of the string.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The content of the string
     */
    public UnclosedString(final String text) {
        super();
        this.text = text;
    }

    @Override
    public final String getErrorMessage() {
        return new StringBuilder()
            .append("Unclosed string: \"")
            .append(this.text)
            .toString();
    }
}
