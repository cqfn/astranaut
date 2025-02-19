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
 * Descriptor representing a pattern in the transformation rule.
 *  This descriptor encapsulates the type of the node, the associated data (such as a static string
 *  or hole), and the child nodes that form subtrees, which are either patterns or holes
 *  themselves. This is used to describe the left side of a transformation rule.
 * @since 1.0.0
 */
public final class PatternDescriptor implements PatternItem {
    /**
     * The type of the node. A pattern is considered matched if the type name matches.
     */
    private final String type;

    /**
     * The data associated with the node. This could be a static string, untyped hole,
     *  or other data descriptor. A pattern is considered matched if the data matches
     *  or is a hole (in which case the data is transferred to the resulting tree).
     */
    private final LeftDataDescriptor data;

    /**
     * The list of child nodes (subtrees) under this node. Each child can be either
     *  a descriptor or a hole. A pattern is considered matched if all of its children are matched.
     *  If a child is a typed hole, then the type is checked and the child is moved to the
     *  resulting subtree. If a child is an untyped hole, then it is moved to the resulting subtree
     *  without checking.
     */
    private final List<PatternItem> children;

    /**
     * The matching compatibility of a pattern descriptor.
     */
    private PatternMatchingMode mode;

    /**
     * Constructs a new {@code PatternDescriptor} with the specified type, data, and list
     *  of child nodes.
     * @param type The type of the node
     * @param data The data associated with the node
     * @param children The list of child nodes (subtrees) under this node
     */
    public PatternDescriptor(final String type, final LeftDataDescriptor data,
        final List<PatternItem> children) {
        this.type = type;
        this.data = data;
        this.children = Collections.unmodifiableList(children);
        this.mode = PatternMatchingMode.NORMAL;
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
    public LeftDataDescriptor getData() {
        return this.data;
    }

    /**
     * Returns an unmodifiable list of child nodes (subtrees) under this node.
     * @return The list of child nodes
     */
    public List<PatternItem> getChildren() {
        return this.children;
    }

    /**
     * Sets the matching compatibility.
     * @param value Matching compatibility
     */
    public void setMatchingMode(final PatternMatchingMode value) {
        this.mode = value;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append('[');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('{');
        }
        builder.append(this.type);
        if (this.data != null) {
            builder.append('<').append(this.data.toString()).append('>');
        }
        if (!this.children.isEmpty()) {
            builder.append('(');
            boolean flag = false;
            for (final PatternItem item : this.children) {
                if (flag) {
                    builder.append(", ");
                }
                builder.append(item.toString());
                flag = true;
            }
            builder.append(')');
        }
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append(']');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('}');
        }
        return builder.toString();
    }
}
