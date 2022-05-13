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
package org.uast.astgen.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable node whose children can be replaced during syntactic tree customization.
 *
 * @since 1.0
 */
public final class ConvertibleNode implements Node {
    /**
     * The parent convertible node.
     */
    private final ConvertibleNode parent;

    /**
     * The prototype node.
     */
    private final Node prototype;

    /**
     * The list of children.
      */
    private final List<Node> children;

    /**
     * Constructor.
     * @param parent The parent convertible node.
     * @param prototype The prototype node.
     */
    private ConvertibleNode(final ConvertibleNode parent, final Node prototype) {
        this.parent = parent;
        this.prototype = prototype;
        this.children = this.initChildrenList();
    }

    /**
     * Constructor.
     * @param prototype The prototype node.
     */
    public ConvertibleNode(final Node prototype) {
        this(null, prototype);
    }

    /**
     * Returns the parent node.
     * @return The parent node
     */
    public ConvertibleNode getParent() {
        return this.parent;
    }

    @Override
    public Fragment getFragment() {
        return this.prototype.getFragment();
    }

    @Override
    public Type getType() {
        return this.prototype.getType();
    }

    @Override
    public String getData() {
        return this.prototype.getData();
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    /**
     * Returns a child, transformed to {@link ConvertibleNode}, by its index.
     * @param index Child index
     * @return Convertible node
     */
    public ConvertibleNode getConvertibleChild(final int index) {
        final Node node = this.children.get(index);
        final ConvertibleNode result;
        if (node instanceof ConvertibleNode) {
            result = (ConvertibleNode) node;
        } else {
            result = new ConvertibleNode(this, node);
            this.children.set(index, result);
        }
        return result;
    }

    @Override
    public List<Node> getChildrenList() {
        return this.children;
    }

    /**
     * Replaces a child node with another node.
     * @param child Child node
     * @param substitute Substitute node
     * @return Result of operation, {@code true} if replacement was successful
     */
    public boolean replaceChild(final Node child, final Node substitute) {
        boolean result = false;
        final int index = this.children.indexOf(child);
        if (index >= 0) {
            this.children.set(index, substitute);
            result = true;
        }
        return result;
    }

    /**
     * Transforms children nodes to convertible ones.
     * @return Array of convertible nodes
     */
    private List<Node> initChildrenList() {
        final int count = this.prototype.getChildCount();
        final List<Node> result = new ArrayList<>(count);
        for (int index = 0; index < count; index = index + 1) {
            result.add(new ConvertibleNode(this, this.prototype.getChild(index)));
        }
        return result;
    }
}
