/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected a data".
 *
 * @since 1.0
 */
public final class ExpectedData extends ParserException {
    /**
     * The descriptor name.
     */
    private final String name;

    /**
     * Constructor.
     * @param name The descriptor name
     */
    public ExpectedData(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected a data: '")
            .append(this.name)
            .append("<#...>' or '")
            .append(this.name)
            .append("<\"...\">'")
            .toString();
    }
}
