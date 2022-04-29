/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.base;

/**
 * Represents a position in source code file.
 *
 * @since 1.0
 */
public interface Position {
    /**
     * Returns absolute position (character index).
     * @return The index
     */
    int getIndex();
}
