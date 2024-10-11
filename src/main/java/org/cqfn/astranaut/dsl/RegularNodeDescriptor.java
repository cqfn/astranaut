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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.cqfn.astranaut.codegen.java.RegularNodeGenerator;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.interpreter.RegularBuilder;

/**
 * Descriptor of a regular node, that is, a node that may have a limited number
 *  of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeDescriptor extends NonAbstractNodeDescriptor {
    /**
     * List of extended child node descriptors (right side of the rule).
     */
    private final List<ChildDescriptorExt> children;

    /**
     * Constructor.
     * @param name Name of the type of the node
     * @param children List of child node descriptors
     */
    public RegularNodeDescriptor(final String name, final List<ChildDescriptorExt> children) {
        super(name);
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append(" <- ");
        if (this.children.isEmpty()) {
            builder.append('0');
        } else {
            boolean flag = false;
            for (final ChildDescriptorExt child : this.children) {
                if (flag) {
                    builder.append(", ");
                }
                flag = true;
                builder.append(child.toString());
            }
        }
        return builder.toString();
    }

    @Override
    public RuleGenerator createGenerator() {
        return new RegularNodeGenerator(this);
    }

    @Override
    public List<ChildDescriptor> getChildTypes() {
        return this.children.stream().map(ChildDescriptorExt::toSimpleDescriptor)
            .collect(Collectors.toList());
    }

    /**
     * Returns list of extended child node descriptors.
     * @return List of child node descriptors
     */
    public List<ChildDescriptorExt> getExtChildTypes() {
        return this.children;
    }

    @Override
    public Builder createBuilder() {
        return new RegularBuilder(this);
    }
}
