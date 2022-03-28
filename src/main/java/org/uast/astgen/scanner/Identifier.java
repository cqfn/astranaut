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
     * The value.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param value The value, i.e. name of the identifier
     */
    public Identifier(final String value) {
        this.value = value;
    }

    /**
     * Returns the value, i.e. name of the identifier
     * @return The value
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
