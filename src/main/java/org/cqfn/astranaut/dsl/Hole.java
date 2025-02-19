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

/**
 * Represents a hole (wildcard) in a pattern matching system, where a hole can be used to match
 *  any node or data within the pattern. Each hole is identified by a non-negative number
 *  (e.g. #1, #2, etc.). Holes are used to capture parts of a syntax tree or data
 *  during pattern matching.
 * @since 1.0.0
 */
public interface Hole extends Comparable<Hole>, PatternChildItem {

    /**
     * Returns the unique number associated with this hole.
     *  The number identifies the specific hole (e.g., #1, #2, etc.).
     * @return The non-negative number of the hole
     */
    int getNumber();

    @Override
    default int compareTo(Hole other) {
        return Integer.compare(this.getNumber(), other.getNumber());
    }
}
