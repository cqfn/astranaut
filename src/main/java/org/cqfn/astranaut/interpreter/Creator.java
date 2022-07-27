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
package org.cqfn.astranaut.interpreter;

import java.util.List;
import java.util.Map;
import org.cqfn.astranaut.base.Builder;
import org.cqfn.astranaut.base.EmptyTree;
import org.cqfn.astranaut.base.Factory;
import org.cqfn.astranaut.base.ListUtils;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.rules.Data;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Hole;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.rules.StringData;

/**
 * Creates a node as described in the descriptor.
 *
 * @since 1.0
 */
public class Creator {
    /**
     * The descriptor.
     */
    private final Descriptor descriptor;

    /**
     * The list of nested creators.
     */
    private final Creator[] subs;

    /**
     * Constructor.
     * @param descriptor The descriptor
     */
    public Creator(final Descriptor descriptor) {
        this.descriptor = descriptor;
        this.subs = new Creator[descriptor.getParameters().size()];
    }

    /**
     * Creates a node as described in the descriptor.
     * @param factory The node factory
     * @param children The collection contains extracted children
     * @param data The collection contains extracted data
     * @return A node
     */
    public Node create(final Factory factory, final Map<Integer, List<Node>> children,
        final Map<Integer, String> data) {
        final Node result;
        if (this.descriptor.getAttribute() == DescriptorAttribute.HOLE) {
            final List<Node> list = children.get(this.descriptor.getHoleNumber());
            if (list == null || list.size() != 1) {
                result = EmptyTree.INSTANCE;
            } else {
                result = list.get(0);
            }
        } else {
            result = this.createFromOrdinaryDescriptor(factory, children, data);
        }
        return result;
    }

    /**
     * Creates a node as described in the "ordinary" descriptor.
     * @param factory The node factory
     * @param children The collection contains extracted children
     * @param data The collection contains extracted data
     * @return A node
     */
    private Node createFromOrdinaryDescriptor(final Factory factory,
        final Map<Integer, List<Node>> children,
        final Map<Integer, String> data) {
        final Builder builder = factory.createBuilder(this.descriptor.getType());
        final ListUtils<Node> list = new ListUtils<>();
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Hole) {
                list.merge(children.get(((Hole) parameter).getValue()));
            } else if (parameter instanceof Descriptor) {
                final Creator creator;
                if (this.subs[index] == null) {
                    creator = new Creator((Descriptor) parameter);
                    this.subs[index] = creator;
                } else {
                    creator = this.subs[index];
                }
                final Node child = creator.create(factory, children, data);
                list.add(child);
            }
            index = index + 1;
        }
        builder.setChildrenList(list.make());
        this.setData(builder, data);
        return builder.createNode();
    }

    /**
     * Sets data to the node builder.
     * @param builder The node builder
     * @param extracted Data, extracted from source syntax tree
     */
    private void setData(final Builder builder, final Map<Integer, String> extracted) {
        final Data data = this.descriptor.getData();
        if (data instanceof Hole) {
            final int index = ((Hole) data).getValue();
            if (extracted.containsKey(index)) {
                builder.setData(extracted.get(index));
            }
        } else if (data instanceof StringData) {
            builder.setData(((StringData) data).getValue());
        }
    }
}
