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

import java.util.Set;
import java.util.TreeSet;

/**
* A counter for tracking placeholders (holes) in a syntax tree descriptor.
* Each hole is identified by a non-negative integer number. The class supports tracking holes
* that replace entire nodes and holes that replace node data separately.
* @since 1.0.0
*/
class HoleCounter {
    /**
     * A set of hole numbers that replace entire nodes in the syntax tree.
     */
    private final Set<Integer> nodes;

    /**
     * A set of hole numbers that replace the data of nodes in the syntax tree.
     */
    private final Set<Integer> data;

    /**
     * Constructs an empty {@code HoleCounter}.
     * Initializes internal sets for tracking node and data holes.
     */
    HoleCounter() {
        this.nodes = new TreeSet<>();
        this.data = new TreeSet<>();
    }

    /**
     * Adds a hole number that replaces an entire node.
     * @param number The hole number to be added (must be non-negative)
     */
    public void addNodeHole(final int number) {
        this.nodes.add(number);
    }

    /**
     * Checks if a hole number exists that replaces an entire node.
     * @param number The hole number to check
     * @return Checking result, {@code true} if the hole number exists, {@code false} otherwise
     */
    public boolean hasNodeHole(final int number) {
        return this.nodes.contains(number);
    }

    /**
     * Adds a hole number that replaces the data of a node.
     * @param number The hole number to be added (must be non-negative)
     */
    public void addDataHole(final int number) {
        this.data.add(number);
    }

    /**
     * Checks if a hole number exists that replaces the data of a node.
     * @param number The hole number to check
     * @return Cheching result, {@code true} if the hole number exists, {@code false} otherwise
     */
    public boolean hasDataHole(final int number) {
        return this.data.contains(number);
    }
}
