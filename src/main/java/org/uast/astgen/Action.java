/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen;

/**
 * The action that a DSL rule handler can perform.
 *
 * @since 1.0
 */
public enum Action {
    /**
     * Generate source code of classes describing the syntax tree,
     * as well as adapters for converting the syntax trees.
     */
    GENERATE,

    /**
     * Load a syntax tree from file, apply the transformation rules and save the result.
     */
    CONVERT
}
