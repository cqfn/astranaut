/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.exceptions;

/**
 * Exception with line number, wraps another exception.
 *
 * @since 1.0
 */
public final class ExceptionWithLineNumber extends BaseException {
    /**
     * Base exception.
     */
    private final BaseException base;

    /**
     * Line number.
     */
    private final int number;

    /**
     * Constructor.
     * @param base Base exception
     * @param number Line number
     */
    public ExceptionWithLineNumber(final BaseException base, final int number) {
        this.base = base;
        this.number = number;
    }

    @Override
    public String getInitiator() {
        return this.base.getInitiator();
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append(this.number)
            .append(": ")
            .append(this.base.getErrorMessage())
            .toString();
    }
}
