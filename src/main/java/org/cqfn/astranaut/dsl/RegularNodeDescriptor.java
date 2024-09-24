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

import java.util.List;

/**
 * A descriptor of a regular node, that is, a node that may have a limited number
 *  of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeDescriptor implements Rule {
    /**
     * Type of node (left side of the rule).
     */
    private final String type;

    /**
     * List of child node descriptors (right side of the rule).
     */
    private final List<ChildDescriptor> children;

    /**
     * Constructor.
     * @param type Type of node
     * @param children List of child node descriptors
     */
    public RegularNodeDescriptor(final String type, final List<ChildDescriptor> children) {
        this.type = type;
        this.children = children;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.type).append(" <- ");
        if (this.children.isEmpty()) {
            builder.append('0');
        } else {
            boolean flag = false;
            for (final ChildDescriptor child : this.children) {
                if (flag) {
                    builder.append(", ");
                }
                flag = true;
                builder.append(child.toString());
            }
        }
        return builder.toString();
    }
}
