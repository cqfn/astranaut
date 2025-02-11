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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
     * Tags and corresponding child descriptors.
     */
    private Map<String, ChildDescriptorExt> tags;

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

    @Override
    public Map<String, ChildDescriptorExt> getTags() {
        final Map<String, ChildDescriptorExt> result;
        if (this.tags == null) {
            result = Collections.emptyMap();
        } else {
            result = Collections.unmodifiableMap(this.tags);
        }
        return result;
    }

    /**
     * Merges the provided map of tags with the current tags, considering all inherited descriptors
     *  of the abstract (base) node. The merge algorithm ensures that only tags matching across
     *  all descendants remain in the abstract node.
     * If the current tags are null, they are initialized with the provided map.
     * If the current tags are not empty, it removes entries whose values match the ones in the
     *  provided map.
     * @param others A map containing tags and their associated values to merge.
     */
    public void mergeTags(final Map<String, ChildDescriptorExt> others) {
        if (this.tags == null) {
            this.tags = new TreeMap<>(others);
        } else if (!this.tags.isEmpty()) {
            final Set<ChildDescriptorExt> replace = new HashSet<>();
            final Set<String> remove = new TreeSet<>();
            for (final Map.Entry<String, ChildDescriptorExt> entry : this.tags.entrySet()) {
                final String tag = entry.getKey();
                final ChildDescriptorExt other = others.get(tag);
                if (other == null) {
                    remove.add(tag);
                    continue;
                }
                final ChildDescriptorExt current = entry.getValue();
                final ChildDescriptorExt substitute = current.merge(other);
                if (substitute == null) {
                    remove.add(tag);
                } else if (!substitute.equals(current)) {
                    replace.add(substitute);
                }
            }
            for (final String tag : remove) {
                this.tags.remove(tag);
            }
            for (final ChildDescriptorExt descr : replace) {
                this.tags.put(descr.getTag(), descr);
            }
        }
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
