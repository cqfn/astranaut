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

import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.core.base.ChildDescriptor;

/**
 * Extended child node descriptor. It's like {@link ChildDescriptor}, but also contains tags.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.DataClass")
public final class ChildDescriptorExt {
    /**
     * Flag indicating that the child node is optional.
     */
    private final boolean optional;

    /**
     * Tag, that is, a name that uniquely identifies this particular child node.
     */
    private final String tag;

    /**
     * Type of child node as a string.
     */
    private final String type;

    /**
     * Rule describing the type of the child node.
     */
    private NodeDescriptor rule;

    /**
     * Constructor.
     *
     * @param optional Flag indicating that the child node is optional
     * @param tag Tag
     * @param type Type of child node as a string
     */
    public ChildDescriptorExt(final boolean optional, final String tag, final String type) {
        this.optional = optional;
        this.tag = tag;
        this.type = type;
    }

    /**
     * Returns a flag indicating that the node described by the descriptor is optional.
     * @return Optional flag.
     */
    public boolean isOptional() {
        return this.optional;
    }

    /**
     * Returns tag, that is, a name that uniquely identifies this particular child node.
     * @return Node tag
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Returns type of the node.
     * @return Node type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets rule describing the type of the child node.
     * @param descriptor Node descriptor
     */
    public void setRule(final NodeDescriptor descriptor) {
        this.rule = descriptor;
    }

    /**
     * Converts this extended descriptor to a simple descriptor for use in the interpreter.
     * @return A simple descriptor (without a tag)
     */
    public ChildDescriptor toSimpleDescriptor() {
        return new ChildDescriptor(this.type, this.optional);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.optional) {
            builder.append('[');
        }
        if (!this.tag.isEmpty()) {
            builder.append(this.tag).append('@');
        }
        builder.append(this.type);
        if (this.optional) {
            builder.append(']');
        }
        return builder.toString();
    }

    /**
     * Merges the current descriptor with another one.
     *  If the tags or types do not match, returns null. If the types differ, creates a new
     *  descriptor based on the first common parent (base descriptor). If both descriptors are
     *  optional, returns the current one. Otherwise, returns the other descriptor.
     * @param other The descriptor to merge with the current one.
     * @return A merged ChildDescriptorExt, or null if the merge is not possible.
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    public ChildDescriptorExt merge(final ChildDescriptorExt other) {
        final ChildDescriptorExt result;
        if (!this.tag.equals(other.tag)) {
            result = null;
        } else if (!this.type.equals(other.type)) {
            result = this.createFromFirstCommonParent(other);
        } else if (this.optional) {
            result = this;
        } else {
            result = other;
        }
        return result;
    }

    /**
     * Creates a new ChildDescriptorExt based on the first common parent between two descriptors.
     *  It compares the topologies of both descriptors and selects the first common node from the
     *  topologies. If no common parent is found, returns {@code null}.
     * @param other The descriptor to compare with the current one.
     * @return A new ChildDescriptorExt based on the first common parent, or {@code null}
     *  if no common parent exists.
     */
    private ChildDescriptorExt createFromFirstCommonParent(final ChildDescriptorExt other) {
        final List<NodeDescriptor> first = this.rule.getTopology();
        final List<NodeDescriptor> second = other.rule.getTopology();
        final List<NodeDescriptor> common = new LinkedList<>(first);
        for (final NodeDescriptor item : first) {
            if (!second.contains(item)) {
                common.remove(item);
            }
        }
        final ChildDescriptorExt result;
        if (common.isEmpty()) {
            result = null;
        } else {
            result = new ChildDescriptorExt(
                this.optional || other.optional,
                this.tag,
                common.get(0).getName()
            );
            result.rule = common.get(0);
        }
        return result;
    }
}
