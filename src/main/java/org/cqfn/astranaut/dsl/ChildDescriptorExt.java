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

import org.cqfn.astranaut.core.base.ChildDescriptor;

/**
 * Extended child node descriptor. It's like {@link ChildDescriptor}, but also contains tags.
 * @since 1.0.0
 */
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
}
