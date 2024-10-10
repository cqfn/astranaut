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
package org.cqfn.astranaut.interpreter;

import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.dsl.LiteralDescriptor;

/**
 * Literal, that is, a node that has data and no child nodes.
 * @since 1.0.0
 */
final class Literal implements Node {
    /**
     * Descriptor resulting from parsing a DSL rule.
     */
    private final LiteralDescriptor descriptor;

    /**
     * Node data.
     */
    private final String data;

    /**
     * Constructor.
     * @param descriptor Descriptor resulting from parsing a DSL rule
     * @param data Node data
     */
    Literal(final LiteralDescriptor descriptor, final String data) {
        this.descriptor = descriptor;
        this.data = data;
    }

    @Override
    public Type getType() {
        return this.descriptor;
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Node getChild(final int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        return Node.toString(this);
    }
}
