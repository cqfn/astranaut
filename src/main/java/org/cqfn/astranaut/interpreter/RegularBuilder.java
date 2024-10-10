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

import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.algorithms.NodeAllocator;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.utils.ListUtils;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Builder that builds regular nodes.
 * @since 1.0.0
 */
public final class RegularBuilder implements Builder {
    /**
     * Descriptor resulting from parsing a DSL rule.
     */
    private final RegularNodeDescriptor descriptor;

    /**
     * Flag indicating that the builder state is valid.
     */
    private boolean valid;

    /**
     * Current set of child nodes.
     */
    private List<Node> children;

    /**
     * Constructor.
     * @param descriptor Descriptor resulting from parsing a DSL rule
     */
    public RegularBuilder(final RegularNodeDescriptor descriptor) {
        this.descriptor = descriptor;
        this.valid = this.descriptor.getExtChildTypes().isEmpty();
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
        final boolean result;
        final List<ChildDescriptor> types = this.descriptor.getChildTypes();
        if (types.isEmpty()) {
            result = list.isEmpty();
        } else {
            final Node[] nodes = new Node[types.size()];
            final NodeAllocator allocator = new NodeAllocator(types);
            result = allocator.allocate(nodes, list);
            if (result) {
                this.valid = true;
                this.children = new ListUtils<Node>().add(nodes).make();
            }
        }
        return result;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public Node createNode() {
        if (!this.isValid()) {
            throw new IllegalStateException();
        }
        return new RegularNode(this.descriptor, this.children);
    }
}
