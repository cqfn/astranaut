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
package org.cqfn.astgen.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree converter built on a set of rules described in DSL.
 *
 * @since 1.0
 */
public class Adapter {
    /**
     * The list of node converters.
     */
    private final List<Converter> converters;

    /**
     * The node factory.
     */
    private final Factory factory;

    /**
     * Constructor.
     * @param converters The list of node converters
     * @param factory The node factory
     */
    public Adapter(final List<Converter> converters, final Factory factory) {
        this.converters = converters;
        this.factory = factory;
    }

    /**
     * Converts the [sub]tree to another, based on DSL rules.
     * @param root The root node of the subtree.
     * @return A converted tree or empty tree if the conversion is impossible
     */
    public Node convert(final Node root) {
        final ConvertibleNode convertible = new ConvertibleNode(root);
        Node result = convertible;
        final List<ConvertibleNode> nodes = new ArrayList<>(0);
        Adapter.buildNodeList(convertible, nodes);
        for (final ConvertibleNode original : nodes) {
            for (final Converter converter : this.converters) {
                final Node transformed = converter.convert(original, this.factory);
                if (!(transformed instanceof EmptyTree)) {
                    final ConvertibleNode parent = original.getParent();
                    if (parent == null) {
                        result = transformed;
                    } else {
                        parent.replaceChild(original, transformed);
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Expands the tree to the node list.
     * @param root Root node
     * @param nodes Resulting list of nodes
     */
    private static void buildNodeList(final ConvertibleNode root,
        final List<ConvertibleNode> nodes) {
        final int count = root.getChildCount();
        for (int index = 0; index < count; index = index + 1) {
            Adapter.buildNodeList(root.getConvertibleChild(index), nodes);
        }
        nodes.add(root);
    }
}
