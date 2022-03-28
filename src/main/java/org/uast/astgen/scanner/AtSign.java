/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * The token represents "at" sign (@).
 *
 * @since 1.0
 */
public final class AtSign implements Token {
    /**
     * The instance.
     */
    public static final Token INSTANCE = new AtSign();

    /**
     * Constructor.
     */
    private AtSign() {
    }

    @Override
    public String toString() {
        return "@";
    }
}
