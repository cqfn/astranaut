/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.HoleAttribute;

/**
 * The hole marker (# and a number after).
 *
 * @since 1.0
 */
public class HoleMarker implements Token {
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
    public HoleMarker(final int value, final HoleAttribute attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder()
            .append('#')
            .append(this.value);
        if (this.attribute == HoleAttribute.ELLIPSIS) {
            builder.append("...");
        }
        return builder.toString();
    }

    /**
     * Creates a hole from this marker.
     * @return A hole
     */
    public Hole createHole() {
        return new Hole(this.value, this.attribute);
    }
}
