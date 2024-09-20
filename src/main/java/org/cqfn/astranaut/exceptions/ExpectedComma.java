/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cqfn.astranaut.exceptions;

import org.cqfn.astranaut.scanner.Token;

/**
 * Exception "Expected a comma after the token".
 *
 * @since 0.1.5
 */
public final class ExpectedComma extends ParserException {
    private static final long serialVersionUID = 6367285536387850138L;

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
