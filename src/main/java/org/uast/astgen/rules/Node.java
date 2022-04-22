/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.uast.astgen.codegen.java.TaggedChild;

/**
 * A rule that describes node.
 *
 * @since 1.0
 */
public final class Node extends Vertex {
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
        final StringBuilder builder = new StringBuilder();
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
    public boolean equals(final Object obj) {
        final Node node;
        boolean equal = false;
        if (obj instanceof Node) {
            node = (Node) obj;
            if (this.type.equals(node.getType())) {
                equal = true;
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public boolean isAbstract() {
        return this.composition.size() == 1 && this.composition.get(0) instanceof Disjunction;
    }

    @Override
    public boolean isTerminal() {
        return this.isList() || this.isOrdinary();
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
}
