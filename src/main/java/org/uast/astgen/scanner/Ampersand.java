/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * The token represents an ampersand.
 *
 * @since 1.0
 */
public final class Ampersand implements Token {
    /**
     * The instance.
     */
    public static final Token INSTANCE = new Ampersand();

    /**
     * Constructor.
     */
    private Ampersand() {
    }

    @Override
    public String toString() {
        return "&";
    }
}
