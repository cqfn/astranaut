/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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
 * A token that contains a sequence of characters (at least one) in quotes, single or double.
 * @since 1.0.0
 */
public abstract class CharSequenceToken extends Token {
    /**
     * Returns the value of the token represented as a string.
     * @return The token value as a string
     */
    public abstract String getValueAsString();

    /**
     * Converts the stored string into a quoted string with escaped special characters.
     * @param quotes The character to use as a quote (either single {@code '} or double {@code "})
     * @return The quoted string with escaped special characters
     */
    public String toQuotedString(final char quotes) {
        return SymbolicToken.toQuotedString(quotes, this.getValueAsString());
    }

    /**
     * Converts the string into a quoted string with escaped special characters.
     * @param quotes The character to use as a quote (either single {@code '} or double {@code "})
     * @param value String value
     * @return The quoted string with escaped special characters
     */
    protected static String toQuotedString(final char quotes, final String value) {
        final StringBuilder builder = new StringBuilder();
        builder.append(quotes);
        for (int index = 0; index < value.length(); index = index + 1) {
            final char chr = value.charAt(index);
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
                    if (quotes == '\'') {
                        builder.append("\\'");
                    } else {
                        builder.append('\'');
                    }
                    break;
                case '\"':
                    if (quotes == '\"') {
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
        builder.append(quotes);
        return builder.toString();
    }
}
