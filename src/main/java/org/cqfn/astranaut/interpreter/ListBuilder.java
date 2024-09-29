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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.dsl.ListNodeDescriptor;

/**
 * Builder that builds list nodes.
 * @since 1.0.0
 */
public final class ListBuilder implements Builder {
    /**
     * Descriptor resulting from parsing a DSL rule.
     */
    private final ListNodeDescriptor descriptor;

    /**
     * Current set of child nodes.
     */
    private List<Node> children;

    /**
     * Constructor.
     * @param descriptor Descriptor resulting from parsing a DSL rule
     */
    public ListBuilder(final ListNodeDescriptor descriptor) {
        this.descriptor = descriptor;
        this.children = Collections.emptyList();
    }

    @Override
    @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
    public void setFragment(final Fragment fragment) {
    }

    @Override
    public boolean setData(final String data) {
        return data.isEmpty();
    }

    @Override
    public boolean setChildrenList(final List<Node> list) {
        boolean result = true;
        for (final Node child : list) {
            result = child.belongsToGroup(this.descriptor.getChildType());
            if (!result) {
                break;
            }
        }
        if (result) {
            this.children = Collections.unmodifiableList(new ArrayList<>(list));
        }
        return result;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Node createNode() {
        return new ListNode(this.descriptor, this.children);
    }
}
