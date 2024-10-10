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

import java.io.File;

/**
 * Determines the location of a piece of DSL code.
 * @since 1.0.0
 */
public final class Location {
    /**
     * Name of the file in which the statement is described.
     */
    private final String filename;

    /**
     * Number of the first line of the statement.
     */
    private final int begin;

    /**
     * Number of the last line of the statement.
     */
    private final int end;

    /**
     * Constructor.
     * @param filename Name of the file in which the statement is described
     * @param begin Number of the first line of the statement
     * @param end Number of the last line of the statement
     */
    Location(final String filename, final int begin, final int end) {
        this.filename = filename;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (!this.filename.isEmpty()) {
            builder.append(new File(this.filename).getName()).append(", ");
        }
        builder.append(this.begin);
        if (this.begin != this.end) {
            builder.append('-').append(this.end);
        }
        return builder.toString();
    }
}
