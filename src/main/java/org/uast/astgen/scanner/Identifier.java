/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * Token that represents identifier.
 *
 * @since 1.0
 */
public final class Identifier implements Token {
    /**
     * The data.
     */
    private final String data;

    /**
     * Constructor.
     *
     * @param data The data, i.e. name of the identifier
     */
    public Identifier(final String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.data;
    }
}
