/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Set;
import java.util.TreeSet;

/**
 * Java imports block.
 *
 * @since 1.0
 */
public final class Imports implements Entity {
    /**
     * The set of imports.
     */
    private final Set<String> set;

    /**
     * Constructor.
     */
    public Imports() {
        this.set = new TreeSet<>();
    }

    /**
     * Adds item to the set.
     * @param item The item
     */
    public void addItem(final String item) {
        this.set.add(item);
    }

    /**
     * Checks whether the set has items.
     * @return Checking result
     */
    public boolean hasItems() {
        return !this.set.isEmpty();
    }

    @Override
    public String generate(final int indent) {
        if (indent != 0) {
            throw new IllegalArgumentException();
        }
        final StringBuilder builder = new StringBuilder();
        for (final String item : this.set) {
            builder.append(item).append('\n');
        }
        builder.append('\n');
        return builder.toString();
    }
}
