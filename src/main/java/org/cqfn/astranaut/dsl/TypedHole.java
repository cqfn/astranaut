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
package org.cqfn.astranaut.dsl;

/**
 * A typed hole, identified by its type and number.
 *  This hole matches only if the node type corresponds to the specified type.
 *  The hole may match any child node of the correct type.
 * @since 1.0.0
 */
public final class TypedHole implements Hole {
    /**
     * The type of the node this hole is intended to match.
     *  For example, "Expression" or "Statement".
     */
    private final String type;

    /**
     * The unique number associated with this hole. For example, #1, #2, etc.
     */
    private final int number;

    /**
     * Constructor to create a typed hole.
     * @param type The type of the node that this hole can match.
     * @param number The unique number for this hole.
     */
    public TypedHole(final String type, final int number) {
        this.type = type;
        this.number = number;
    }

    /**
     * Returns the type of the node that this hole is intended to match.
     * @return The type of the node (e.g., "Expression", "Statement").
     */
    public String getType() {
        return this.type;
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return String.format("%s#%d", this.type, this.number);
    }
}
