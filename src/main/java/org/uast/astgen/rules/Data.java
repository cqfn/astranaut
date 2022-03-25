/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

/**
 * Data that can be inside descriptor.
 *
 * @since 1.0
 */
public interface Data {
    @Override
    String toString();

    /**
     * Returns {@code true} if data is valid.
     * @return The flag
     */
    boolean isValid();
}
