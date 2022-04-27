/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * The token represents an empty node (with no data or children).
 *
 * @since 1.0
 */
public final class EmptySymbol implements Token {
    /**
     * The instance.
     */
    public static final Token INSTANCE = new EmptySymbol();

    /**
     * Constructor.
     */
    private EmptySymbol() {
    }

    @Override
    public String toString() {
        return "0";
    }
}
