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
package org.cqfn.astranaut.codegen.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.utils.Pair;

/**
 * Tries to break a long line of source code into multiple lines, so that the resulting lines fit
 *  within the allowed {@link SourceCodeBuilder#MAX_LINE_LENGTH}.
 * @since 1.0.0
 */
final class LongLineBreaker {
    /**
     * The rest of the line.
     */
    private String tail;

    /**
     * Indentation from the beginning of the line (used for calculations,
     *  but not included in the result).
     */
    private final int indent;

    /**
     * Additional offset to the indentation.
     */
    private final int offset;

    /**
     * Second additional offset.
     */
    private int bias;

    /**
     * Constructor.
     * @param code Source code line
     * @param indent Indentation from the beginning of the line
     * @param offset Additional offset to the indentation
     */
    LongLineBreaker(final String code, final int indent, final int offset) {
        this.tail = code;
        this.indent = indent;
        this.offset = offset;
    }

    /**
     * Attempts to split a source code line into multiple indented parts
     *  based on known patterns such as assignment, logical AND, or method call chains.
     *  Returns the result as a list of (line, indent) pairs.
     *  If no pattern matches, returns empty list.
     * @return List of indented line parts, or empty if no split was applicable
     */
    List<Pair<String, Integer>> split() {
        List<Pair<String, Integer>> lines = new ArrayList<>(2);
        do {
            if (this.splitByAssignment(lines)) {
                break;
            }
            if (this.splitByAndOperator(lines)) {
                break;
            }
            if (this.splitByCallChain(lines)) {
                break;
            }
            lines = Collections.emptyList();
        } while (false);
        return lines;
    }

    /**
     * Tries to split the line by assignment (`=`) operator.
     *  If successful, adds the left-hand part and updates internal state for further splitting.
     * @param lines Output list of line parts with indentation
     * @return End of operation flag, if no further separation is no longer required
     */
    private boolean splitByAssignment(final List<Pair<String, Integer>> lines) {
        boolean result = false;
        final int index = this.tail.indexOf(" = ");
        if (index > 0) {
            lines.add(new Pair<>(this.tail.substring(0, index + 2), this.offset));
            this.tail = this.tail.substring(index + 3).trim();
            this.bias = 1;
        }
        if (this.bias == 1
            && SourceCodeBuilder.tryOn(this.indent + this.offset + 1, this.tail)) {
            lines.add(new Pair<>(this.tail, this.offset + 1));
            result = true;
        }
        return result;
    }

    /**
     * Tries to split the line by logical AND (`&&`) operator.
     *  If successful, adds split parts and updates internal state accordingly.
     * @param lines Output list of line parts with indentation
     * @return End of operation flag, if no further separation is no longer required
     */
    private boolean splitByAndOperator(final List<Pair<String, Integer>> lines) {
        boolean result = false;
        int index = this.tail.indexOf("&&");
        while (!result && index > 0) {
            lines.add(
                new Pair<>(
                    this.tail.substring(0, index).trim(),
                    this.offset + this.bias
                )
            );
            this.tail = this.tail.substring(index).trim();
            this.bias = 1;
            if (SourceCodeBuilder.tryOn(this.indent + this.offset + 1, this.tail)) {
                lines.add(new Pair<>(this.tail, this.offset + 1));
                result = true;
            }
            index = this.tail.indexOf("&&", 2);
        }
        return result;
    }

    /**
     * Tries to split the line by method call chaining (`).`).
     *  If successful, adds split parts and updates internal state accordingly.
     *  @param lines Output list of line parts with indentation
     *  @return End of operation flag, if no further separation is no longer required
     */
    private boolean splitByCallChain(final List<Pair<String, Integer>> lines) {
        boolean result = false;
        int index = this.tail.indexOf(").");
        while (!result && index > 0) {
            lines.add(
                new Pair<>(
                    this.tail.substring(0, index + 1).trim(),
                    this.offset + this.bias
                )
            );
            this.tail = this.tail.substring(index + 1).trim();
            this.bias = 1;
            if (SourceCodeBuilder.tryOn(this.indent + this.offset + 1, this.tail)) {
                lines.add(new Pair<>(this.tail, this.offset + 1));
                result = true;
            }
            index = this.tail.indexOf(").", 2);
        }
        return result;
    }
}
