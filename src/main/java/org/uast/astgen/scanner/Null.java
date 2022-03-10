/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * The null token (that represents nothing).
 *
 * @since 1.0
 */
public final class Null implements Token {
    /**
     * The instance.
     */
    public static final Token INSTANCE = new Null();

    /**
     * Constructor.
     */
    private Null() {
    }

    @Override
    public String toString() {
        return "<null>";
    }
}
