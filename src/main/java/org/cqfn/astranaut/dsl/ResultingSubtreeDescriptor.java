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

/**
 * Descriptor representing the resulting subtree after a transformation.
 *  This descriptor encapsulates the node type, its associated data, and all its child nodes,
 *  which themselves are subtrees.
 * @since 1.0.0
 */
public final class ResultingSubtreeDescriptor implements ResultingItem {
    /**
     * The type of the node (e.g., the left side of the rule).
     */
    private final String type;

    /**
     * The data associated with the node. This could be a static string, untyped hole,
     *  or other data descriptor.
     */
    private final DataDescriptorExt data;

    /**
     * The list of child nodes (subtrees) under this node. Each child can be either
     *  a descriptor or another hole.
     */
    private final List<ResultingItem> children;

    /**
     * Constructs a new {@code ResultingSubtreeDescriptor} with the specified type, data, and list
     *  of child nodes.
     * @param type The type of the node
     * @param data The data associated with the node
     * @param children The list of child nodes (subtrees) under this node
     */
    public ResultingSubtreeDescriptor(final String type, final DataDescriptorExt data,
        final List<ResultingItem> children) {
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
    public DataDescriptorExt getData() {
        return this.data;
    }

    /**
     * Returns an unmodifiable list of child nodes (subtrees) under this node.
     * @return The list of child nodes
     */
    public List<ResultingItem> getChildren() {
        return this.children;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.type);
        if (this.data != null) {
            builder.append('<').append(this.data.toString()).append('>');
        }
        if (this.children != null && !this.children.isEmpty()) {
            builder.append('(');
            boolean flag = false;
            for (final ResultingItem item : this.children) {
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
}
