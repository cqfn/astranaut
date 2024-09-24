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
package org.cqfn.astranaut.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Source code builder. Creates source code from indented lines.
 * @since 1.0.0
 */
public final class SourceCodeBuilder {
    /**
     * One tab used as an indentation unit (four spaces).
     */
    private static final String TABULATION = "    ";

    /**
     * Indented source code lines.
     */
    private final List<Line> lines = new ArrayList<>(128);

    /**
     * Adds one or more lines of source code with the specified indentation.
     * @param indent Indentation
     * @param text Program text
     */
    public void add(final int indent, final String text) {
        for (final String line : text.split("\n")) {
            this.lines.add(new Line(indent, line));
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final Line line : this.lines) {
            line.build(builder);
        }
        return builder.toString();
    }

    /**
     * One line of source code.
     * @since 1.0.0
     */
    private static final class Line {
        /**
         * Indentation.
         */
        private final int indent;

        /**
         * Program text.
         */
        private final String text;

        /**
         * Constructor.
         * @param indent Indentation
         * @param text Program text
         */
        private Line(final int indent, final String text) {
            this.indent = indent;
            this.text = text;
        }

        /**
         * Builds the source code.
         * @param builder Where to build
         */
        void build(final StringBuilder builder) {
            for (int counter = 0; counter < this.indent; counter = counter + 1) {
                builder.append(SourceCodeBuilder.TABULATION);
            }
            builder.append(this.text).append('\n');
        }
    }
}
