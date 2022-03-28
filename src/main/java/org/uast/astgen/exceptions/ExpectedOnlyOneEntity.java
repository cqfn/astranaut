/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Expected only one entity".
 *
 * @since 1.0
 */
public final class ExpectedOnlyOneEntity extends ParserException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text
     */
    public ExpectedOnlyOneEntity(final String text) {
        super();
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected only one entity: '")
            .append(this.text)
            .append('\'')
            .toString();
    }
}
