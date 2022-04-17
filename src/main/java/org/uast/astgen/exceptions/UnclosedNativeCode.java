/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Unclosed native code".
 *
 * @since 1.0
 */
public class UnclosedNativeCode extends ParserException {
    /**
     * The code.
     */
    private final String code;

    /**
     * Constructor.
     * @param code The code
     */
    public UnclosedNativeCode(final String code) {
        super();
        this.code = code;
    }

    @Override
    public final String getErrorMessage() {
        return new StringBuilder()
            .append("Unclosed native code: $")
            .append(this.code)
            .toString();
    }
}
