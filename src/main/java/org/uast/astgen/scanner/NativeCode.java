/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * Token that represents native [Java] code.
 *
 * @since 1.0
 */
public final class NativeCode implements Token {
    /**
     * The code.
     */
    private final String code;

    /**
     * Constructor.
     * @param code The code
     */
    public NativeCode(final String code) {
        this.code = code;
    }

    /**
     * Returns the code.
     * @return The code
     */
    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return String.format("$%s$", this.code);
    }
}
