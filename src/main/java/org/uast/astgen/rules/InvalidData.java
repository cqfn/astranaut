/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

/**
 * Invalid {@link Data} object.
 *
 * @since 1.0
 */
public final class InvalidData implements Data {
    /**
     * The instance.
     */
    public static final InvalidData INSTANCE = new InvalidData();

    /**
     * Private constructor.
     */
    private InvalidData() {
    }

    @Override
    public String toString() {
        return "<invalid>";
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
