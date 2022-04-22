/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

/**
 * A rule that describes literal.
 *
 * @since 1.0
 */
public final class Transformation implements Rule {
    /**
     * Left part.
     */
    private final Descriptor left;

    /**
     * Right part.
     */
    private final Descriptor right;

    /**
     * Constructor.
     * @param left Left "from" part
     * @param right Right "to" part
     */
    public Transformation(final Descriptor left, final Descriptor right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Returns the left part of the rule.
     * @return Descriptor of the left part
     */
    public Descriptor getLeft() {
        return this.left;
    }

    /**
     * Returns the right part of the rule.
     * @return Descriptor of the right part
     */
    public Descriptor getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", this.left.toString(), this.right.toString());
    }
}
