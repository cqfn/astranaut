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
package org.cqfn.astranaut.dsl;

import java.util.Objects;

/**
 * Entity that represents a static string.
 * @since 1.0.0
 */
public final class StaticString implements Comparable<StaticString>, DataDescriptor {
    /**
     * The value of the static string.
     */
    private final String value;

    /**
     * Constructor to initialize the StaticString with the provided value.
     * @param value The string value
     */
    public StaticString(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Gets the value of the static string.
     * @return The string value
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public int compareTo(final StaticString other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        final StringBuilder escaped = new StringBuilder(this.value.length() + 10);
        escaped.append('"');
        for (final char symbol : this.value.toCharArray()) {
            switch (symbol) {
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\"':
                    escaped.append("\\\"");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(symbol);
                    break;
            }
        }
        escaped.append('"');
        return escaped.toString();
    }
}
