/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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

package org.uast.astgen.utils;

/**
 * Some string utilities.
 *
 * @since 1.0
 */
public class StringUtils {
    /**
     * The space ' ' symbol.
     */
    public static final StringUtils SPACE = new StringUtils(" ");

    /**
     * Value to be processed.
     */
    private final String value;

    /**
     * Constructor.
     * @param value Value to be processed.
     */
    public StringUtils(final String value) {
        this.value = value;
    }

    /**
     * Replaces entities (brackets, line breaks) in the string.
     * @return Result
     */
    public String escapeEntities() {
        final StringBuilder builder = new StringBuilder();
        final int length = this.value.length();
        for (int index = 0; index < length; index = index + 1) {
            final char symbol = this.value.charAt(index);
            switch (symbol) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\"':
                    builder.append("\\\"");
                    break;
                case '\'':
                    builder.append("\\'");
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
                default:
                    builder.append(symbol);
                    break;
            }
        }
        return builder.toString();
    }

    /**
     * Repeats the original string multiple times.
     * @param count Number of times
     * @return Generated string
     */
    public String repeat(final int count) {
        final StringBuilder builder = new StringBuilder(this.value.length() * count);
        int counter = count;
        while (counter > 0) {
            builder.append(this.value);
            counter = counter - 1;
        }
        return builder.toString();
    }
}
