/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

/**
 * The hole attribute.
 *
 * @since 1.1
 */
public enum HoleAttribute {
    /**
     * Hole has no attribute (replaces one child).
     */
    NONE,

    /**
     * The hole replaces all remaining children.
     */
    ELLIPSIS
}
