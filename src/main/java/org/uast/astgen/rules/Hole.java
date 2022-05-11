/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.Objects;

/**
 * Hole, i.e. #1, #2, etc.
 *
 * @since 1.0
 */
public final class Hole implements Data, Parameter {
    /**
     * Value of the hole.
     */
    private final int value;

    /**
     * Attribute.
     */
    private final HoleAttribute attribute;

    /**
     * Constructor.
     * @param value Value of the hole
     * @param attribute Attribute
     */
    public Hole(final int value, final HoleAttribute attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    /**
     * Returns the value of the hole.
     * @return The value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the attribute of the hole.
     * @return The attribute
     */
    public HoleAttribute getAttribute() {
        return this.attribute;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
            .append('#')
            .append(this.value);
        if (this.attribute == HoleAttribute.ELLIPSIS) {
            builder.append("...");
        }
        return builder.toString();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        final Hole hole;
        boolean equal = false;
        if (obj instanceof Hole) {
            hole = (Hole) obj;
            if (this.value == hole.getValue()) {
                equal = true;
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }
}
