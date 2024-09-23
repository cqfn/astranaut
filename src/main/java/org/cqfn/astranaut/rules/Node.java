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

package org.cqfn.astranaut.rules;

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.codegen.java.TaggedChild;
import org.cqfn.astranaut.utils.StringUtils;

/**
 * A rule that describes node.
 *
 * @since 0.1.5
 */
public final class Node extends Vertex {
    /**
     * String representation of the node.
     */
    private String string;

    /**
     * Left part.
     */
    private final String type;

    /**
     * Right part.
     */
    private final List<Child> composition;

    /**
     * Constructor.
     * @param type The type name (left part).
     * @param composition The composition (right part).
     */
    public Node(final String type, final List<Child> composition) {
        this.type = type;
        this.composition = composition;
    }

    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Returns right part of the node.
     * @return The composition.
     */
    public List<Child> getComposition() {
        return this.composition;
    }

    @Override
    public String toString() {
        if (this.string == null) {
            this.string = this.asString();
        }
        return this.string;
    }

    @Override
    public void toStringIndented(final StringBuilder builder, final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent);
        builder
            .append(tabulation)
            .append(this.type)
            .append('\n')
            .append(tabulation)
            .append("<-\n")
            .append(tabulation);
        boolean flag = false;
        for (final Child child : this.composition) {
            if (flag) {
                builder.append(", ");
            }
            builder.append(child.toString());
            flag = true;
        }
        builder.append('\n');
    }

    /**
     * Checks if the node has at least one optional child.
     * @return Checking result
     */
    public boolean hasOptionalChild() {
        boolean result = false;
        for (final Child child : this.composition) {
            if (child instanceof Descriptor
                && ((Descriptor) child).getAttribute() == DescriptorAttribute.OPTIONAL) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean isOrdinary() {
        boolean result = true;
        for (final Child child : this.composition) {
            if (!(child instanceof Descriptor)) {
                result = false;
                break;
            }
            if (((Descriptor) child).getAttribute() == DescriptorAttribute.LIST) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean isAbstract() {
        return this.composition.size() == 1 && this.composition.get(0) instanceof Disjunction;
    }

    @Override
    public boolean isFinal() {
        return this.isList()
            || this.isOrdinary()
            || this.isEmpty();
    }

    /**
     * Checks if the node is list.
     * @return Checking result
     */
    public boolean isList() {
        boolean result = false;
        if (this.composition.size() == 1) {
            final Child child = this.composition.get(0);
            if (child instanceof Descriptor
                && ((Descriptor) child).getAttribute() == DescriptorAttribute.LIST) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Checks if the node is empty.
     * @return Checking result
     */
    public boolean isEmpty() {
        boolean result = false;
        if (this.composition.size() == 1) {
            final Child child = this.composition.get(0);
            if (child.equals(Empty.INSTANCE)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns list of tags.
     * @return The list of tags
     */
    public List<TaggedChild> getTags() {
        final List<TaggedChild> result = new ArrayList<>(this.composition.size());
        for (final Child child : this.composition) {
            if (child instanceof Descriptor) {
                final Descriptor descriptor = (Descriptor) child;
                final String tag = descriptor.getTag();
                if (!tag.isEmpty()) {
                    result.add(
                        new TaggedChild() {
                            @Override
                            public String getTag() {
                                return tag;
                            }

                            @Override
                            public String getType() {
                                return descriptor.getType();
                            }

                            @Override
                            public boolean isOverridden() {
                                return false;
                            }
                        }
                    );
                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(final Vertex obj) {
        return this.toString().compareTo(obj.toString());
    }

    /**
     * Creates string representation of the node
     * @return String representation of the node
     */
    private String asString() {
        final StringBuilder builder = new StringBuilder(128);
        builder.append(this.type).append(" <- ");
        boolean flag = false;
        for (final Child child : this.composition) {
            if (flag) {
                builder.append(", ");
            }
            builder.append(child.toString());
            flag = true;
        }
        return builder.toString();
    }
}
