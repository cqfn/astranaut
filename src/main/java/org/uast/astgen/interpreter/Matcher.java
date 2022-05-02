/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.Map;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Descriptor;

/**
 * Matcher that works with the raw descriptor.
 *
 * @since 1.0
 */
public final class Matcher implements org.uast.astgen.base.Matcher {
    /**
     * The descriptor.
     */
    private final Descriptor descriptor;

    /**
     * Constructor.
     * @param descriptor The descriptor
     */
    public Matcher(final Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public boolean match(final Node node, final Map<Integer, Node> children,
        final Map<Integer, String> data) {
        return this.checkType(node) && this.checkChildCount(node);
    }

    /**
     * Checks the type matches.
     * @param node The node
     * @return Checking result, {@code true} if the type matches
     */
    private boolean checkType(final Node node) {
        return node.getType().getName().equals(this.descriptor.getType());
    }

    /**
     * Checks the number of child nodes matches.
     * @param node The node
     * @return Checking result, {@code true} if the number of child nodes matches
     */
    private boolean checkChildCount(final Node node) {
        return node.getChildCount() == this.descriptor.getParameters().size();
    }
}
