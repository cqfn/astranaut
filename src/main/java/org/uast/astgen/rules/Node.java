/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.rules;

import java.util.List;

/**
 * A rule that describes node.
 *
 * @since 1.0
 */
public class Node implements Rule {
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
    public final String toString() {
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

    @Override
    public final void generate() {
        throw new IllegalStateException();
    }
}
