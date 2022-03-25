/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.uast.astgen.rules.Hole;

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
     * Constructor.
     *
     * @param value Value of the hole.
     */
    public HoleMarker(final int value) {
        this.value = value;
    }

    @Override
    public final String toString() {
        return new StringBuilder()
            .append('#')
            .append(this.value)
            .toString();
    }

    /**
     * Creates a hole from this marker.
     * @return A hole
     */
    public Hole createHole() {
        return new Hole(this.value);
    }
}
