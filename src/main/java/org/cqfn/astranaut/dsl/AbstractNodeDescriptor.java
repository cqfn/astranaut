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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.cqfn.astranaut.codegen.java.AbstractNodeGenerator;
import org.cqfn.astranaut.codegen.java.RuleGenerator;

/**
 * The descriptor of an abstract node, that is, a node from which other nodes
 *  (abstract and non-abstract) inherit. An abstract node cannot be instantiated.
 *  When generating Java code, an interface is created from such a descriptor.
 * @since 1.0.0
 */
public final class AbstractNodeDescriptor extends NodeDescriptor {
    /**
     * List of types that inherit from this type.
     */
    private final List<String> subtypes;

    /**
     * Constructor.
     * @param name Name of the type of the node (left side of the rule)
     * @param subtypes List of types that inherit from this type
     */
    public AbstractNodeDescriptor(final String name, final List<String> subtypes) {
        super(name);
        this.subtypes = AbstractNodeDescriptor.checkSubtypeList(subtypes);
    }

    /**
     * Returns list of types that inherit from this type.
     * @return Subtypes list
     */
    public List<String> getSubtypes() {
        return this.subtypes;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append(" <- ");
        boolean flag = false;
        for (final String type : this.subtypes) {
            if (flag) {
                builder.append(" | ");
            }
            flag = true;
            builder.append(type);
        }
        if (this.subtypes.size() == 1) {
            builder.append(" | ?");
        }
        return builder.toString();
    }

    @Override
    public RuleGenerator createGenerator() {
        return new AbstractNodeGenerator(this);
    }

    /**
     * Checks the list of subtypes. It must be non-empty.
     * @param subtypes List of types that inherit from this type
     * @return Verified non-mutable list without repeating values
     */
    private static List<String> checkSubtypeList(final List<String> subtypes) {
        if (subtypes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return Collections.unmodifiableList(
            new ArrayList<>(
                new LinkedHashSet<>(subtypes)
            )
        );
    }
}
