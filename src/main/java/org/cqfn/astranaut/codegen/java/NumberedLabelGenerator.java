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

/**
 * Generates sequentially numbered labels based on a given prefix.
 * @since 1.0.0
 */
public final class NumberedLabelGenerator {
    /**
     * The base label prefix.
     */
    private final String prefix;

    /**
     * The current numeric suffix.
     */
    private int number;

    /**
     * Constructor.
     * @param prefix The prefix for the generated labels
     */
    public NumberedLabelGenerator(final String prefix) {
        this.prefix = prefix;
        this.number = 0;
    }

    /**
     * Generates the next numbered label.
     *  The method appends the current number to the label and increments the counter.
     *  For example, if the label is {@code "Item"}, the sequence will be:
     *  {@code "Item0"}, {@code "Item1"}, {@code "Item2"}, ...
     * @return The next generated label.
     */
    public String getLabel() {
        final String result = String.format("%s%d", this.prefix, this.number);
        this.number = this.number + 1;
        return result;
    }
}
