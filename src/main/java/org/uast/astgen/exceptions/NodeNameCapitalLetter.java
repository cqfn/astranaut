/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

import java.util.Locale;

/**
 * Exception "Node names must start with a capital letter".
 *
 * @since 1.0
 */
public final class NodeNameCapitalLetter extends ParserException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text
     */
    public NodeNameCapitalLetter(final String text) {
        super();
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Node names must start with a capital letter: '")
            .append(this.text.substring(0, 1).toUpperCase(Locale.ENGLISH))
            .append(this.text.substring(1))
            .append('\'')
            .toString();
    }
}
