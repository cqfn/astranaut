/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Empty data literal".
 *
 * @since 1.0
 */
public final class EmptyDataLiteral extends ParserException {
    /**
     * The descriptor name.
     */
    private final String name;

    /**
     * Constructor.
     * @param name The descriptor name
     */
    public EmptyDataLiteral(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Empty data literal: '")
            .append(this.name)
            .append("<>'")
            .toString();
    }
}
