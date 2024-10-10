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
import java.util.stream.Collectors;
import org.cqfn.astranaut.core.base.Type;

/**
 * Descriptor of a non-abstract node, that is, a node that can be instantiated.
 *  No other nodes inherit from this kind of node. During Java code generation, the final
 *  classes are produced from such a descriptor. In addition, these descriptors support the
 *  {@link Type} interface for use in the interpreter.
 * @since 1.0.0
 */
public abstract class NonAbstractNodeDescriptor extends NodeDescriptor implements Type {
    /**
     * Constructor.
     * @param name Name of the type of the node (left side of the rule)
     */
    public NonAbstractNodeDescriptor(final String name) {
        super(name);
    }

    @Override
    public final List<String> getHierarchy() {
        return this.getTopology().stream().map(NodeDescriptor::getName)
            .collect(Collectors.toList());
    }
}
