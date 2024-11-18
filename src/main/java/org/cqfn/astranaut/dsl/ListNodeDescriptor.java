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
import org.cqfn.astranaut.codegen.java.ListNodeGenerator;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.interpreter.ListBuilder;

/**
 * Descriptor of a list node, that is, one that can contain an unlimited number of child nodes
 *  of the same type.
 * @since 1.0.0
 */
public final class ListNodeDescriptor extends NonAbstractNodeDescriptor {
    /**
     * Name of the children node type.
     */
    private final String type;

    /**
     * Constructor.
     * @param name Name of the type of the node (left side of the rule)
     * @param type Name of the children node type
     */
    public ListNodeDescriptor(final String name, final String type) {
        super(name);
        this.type = type;
    }

    /**
     * Returns the name of the children node type.
     * @return Name of the children node type
     */
    public String getChildType() {
        return this.type;
    }

    @Override
    public List<ChildDescriptor> getChildTypes() {
        return Collections.singletonList(new ChildDescriptor(this.type));
    }

    @Override
    public String toString() {
        return String.format("%s <- {%s}", this.getName(), this.type);
    }

    @Override
    public RuleGenerator createGenerator() {
        return new ListNodeGenerator(this);
    }

    @Override
    public Builder createBuilder() {
        return new ListBuilder(this);
    }
}
