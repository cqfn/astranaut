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
package org.cqfn.astranaut.parser;

/**
 * Token representing a string in single or double quotes.
 * @since 1.0.0
 */
public final class StringToken extends Token {
    /**
     * Quotation mark that opens and closes a string.
     */
    private final char quote;

    /**
     * Value of the token.
     */
    private final String value;

    /**
     * Constructor.
     * @param quote Quotation mark that opens and closes a string
     * @param value Value of the token
     */
    public StringToken(final char quote, final String value) {
        this.quote = StringToken.checkQuote(quote);
        this.value = value;
    }

    /**
     * Returns value of the token.
     * @return String value of the token
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.quote);
        for (int index = 0; index < this.value.length(); index = index + 1) {
            final char chr = this.value.charAt(index);
            switch (chr) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\'':
                    if (this.quote == '\'') {
                        builder.append("\\'");
                    } else {
                        builder.append('\'');
                    }
                    break;
                case '\"':
                    if (this.quote == '\"') {
                        builder.append("\\\"");
                    } else {
                        builder.append('"');
                    }
                    break;
                default:
                    builder.append(chr);
                    break;
            }
        }
        builder.append(this.quote);
        return builder.toString();
    }

    /**
     * Checks the “quote” parameter to see if it is valid.
     * @param quote Quote parameter
     * @return The same quote
     */
    private static char checkQuote(final char quote) {
        if (quote != '\'' && quote != '\"') {
            throw new IllegalArgumentException();
        }
        return quote;
    }
}
