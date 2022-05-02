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
     * Constructor.
     * @param value Value of the hole
     */
    public Hole(final int value) {
        this.value = value;
    }

    /**
     * Returns the value of the hole.
     * @return The value
     */
    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append('#')
            .append(this.value)
            .toString();
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
