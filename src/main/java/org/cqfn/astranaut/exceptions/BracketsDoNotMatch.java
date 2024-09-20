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

/**
 * Exception "Opening and closing brackets do not match".
 *
 * @since 0.1.5
 */
public class BracketsDoNotMatch extends ParserException {
    private static final long serialVersionUID = 3342925893715842858L;

    /**
     * Opening bracket.
     */
    private final char opening;

    /**
     * Closing bracket.
     */
    private final char closing;

    /**
     * Constructor.
     * @param opening Opening bracket
     * @param closing Closing bracket
     */
    public BracketsDoNotMatch(final char opening, final char closing) {
        this.opening = opening;
        this.closing = closing;
    }

    @Override
    public final String getErrorMessage() {
        return new StringBuilder()
            .append("Opening \'")
            .append(this.opening)
            .append("\' and closing \'")
            .append(this.closing)
            .append("\' brackets does not match")
            .toString();
    }
}
