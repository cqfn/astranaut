/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

import org.uast.astgen.scanner.Token;

/**
 * Exception "Expected a comma after the token".
 *
 * @since 1.0
 */
public final class ExpectedComma extends ParserException {
    /**
     * The token.
     */
    private final Token token;

    /**
     * Constructor.
     * @param token The token
     */
    public ExpectedComma(final Token token) {
        super();
        this.token = token;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Expected a comma after the token: '")
            .append(this.token.toString())
            .append('\'')
            .toString();
    }
}
