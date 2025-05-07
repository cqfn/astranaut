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

import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.utils.ListUtils;

/**
 * Descriptor representing the resulting subtree after a transformation.
 *  This descriptor encapsulates the node type, its associated data, and all its child nodes,
 *  which themselves are subtrees.
 * @since 1.0.0
 */
public final class ResultingSubtreeDescriptor implements RightSideItem {
    /**
     * The type of the node.
     */
    private final String type;

    /**
     * The data associated with the node. This could be a static string, untyped hole,
     *  or other data descriptor.
     */
    private final RightDataDescriptor data;

    /**
     * The list of child nodes (subtrees) under this node. Each child can be either
     *  a descriptor or a hole.
     */
    private final List<RightSideItem> children;

    /**
     * Constructs a new {@code ResultingSubtreeDescriptor} with the specified type, data, and list
     *  of child nodes.
     * @param type The type of the node
     * @param data The data associated with the node
     * @param children The list of child nodes (subtrees) under this node
     */
    public ResultingSubtreeDescriptor(final String type, final RightDataDescriptor data,
        final List<RightSideItem> children) {
        this.type = type;
        this.data = data;
        this.children = Collections.unmodifiableList(children);
    }

    /**
     * Returns the type of the node.
     * @return The type of the node
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the data associated with this node.
     * @return The data descriptor of the node
     */
    public RightDataDescriptor getData() {
        return this.data;
    }

    /**
     * Returns an unmodifiable list of child nodes (subtrees) under this node.
     * @return The list of child nodes
     */
    public List<RightSideItem> getChildren() {
        return this.children;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.type);
        if (this.data != null) {
            builder.append('<').append(this.data.toString()).append('>');
        }
        if (!this.children.isEmpty()) {
            builder.append('(');
            boolean flag = false;
            for (final RightSideItem item : this.children) {
                if (flag) {
                    builder.append(", ");
                }
                builder.append(item.toString());
                flag = true;
            }
            builder.append(')');
        }
        return builder.toString();
    }

    /**
     * Checks whether all children are untyped holes.
     * @return Checking result, {@code true} if all children are holes
     */
    public boolean allChildrenAreHoles() {
        boolean result = true;
        if (this.children.isEmpty()) {
            result = false;
        } else {
            for (final RightSideItem item : this.children) {
                if (!(item instanceof UntypedHole)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Creates a node based on this descriptor and the extracted nodes and data.
     * @param extracted Extracted nodes and data
     * @param factory Factory for creating nodes
     * @param fragment The fragment that is covered by the created node
     * @return Created node or dummy node if node can't be created
     */
    public Node createNode(final Extracted extracted, final Factory factory,
        final Fragment fragment) {
        Node result = DummyNode.INSTANCE;
        do {
            final Builder builder = factory.createBuilder(this.type);
            builder.setFragment(fragment);
            boolean flag;
            if (this.data instanceof StaticString) {
                flag = builder.setData(((StaticString) this.data).getValue());
            } else {
                flag = builder.setData(extracted.getData(((UntypedHole) this.data).getNumber()));
            }
            if (!flag) {
                break;
            }
            if (this.children.isEmpty()) {
                flag = builder.setChildrenList(Collections.emptyList());
            } else {
                flag = builder.setChildrenList(this.createChildrenList(extracted, factory));
            }
            if (!flag || !builder.isValid()) {
                break;
            }
            result = builder.createNode();
        } while (false);
        return result;
    }

    /**
     * Creates a list of child nodes based on this descriptor and the extracted nodes and data.
     * @param extracted Extracted nodes and data
     * @param factory Factory for creating nodes
     * @return Created children list
     */
    private List<Node> createChildrenList(final Extracted extracted, final Factory factory) {
        final ListUtils<Node> list = new ListUtils<>();
        for (final RightSideItem item : this.children) {
            if (item instanceof UntypedHole) {
                list.add(extracted.getNodes(((UntypedHole) item).getNumber()));
            } else {
                final ResultingSubtreeDescriptor descriptor = (ResultingSubtreeDescriptor) item;
                list.add(descriptor.createNode(extracted, factory, EmptyFragment.INSTANCE));
            }
        }
        return list.make();
    }
}
