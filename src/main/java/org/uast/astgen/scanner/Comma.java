/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * The token represents a comma.
 *
 * @since 1.0
 */
public final class Comma implements Token {
    /**
     * The instance.
     */
    public static final Token INSTANCE = new Comma();

    /**
     * Constructor.
     */
    private Comma() {
    }

    @Override
    public String toString() {
        return ",";
    }
}
