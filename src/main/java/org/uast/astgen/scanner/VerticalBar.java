/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * The token represents vertical bar.
 *
 * @since 1.0
 */
public final class VerticalBar implements Token {
    /**
     * The instance.
     */
    public static final Token INSTANCE = new VerticalBar();

    /**
     * Constructor.
     */
    private VerticalBar() {
    }

    @Override
    public String toString() {
        return "|";
    }
}
