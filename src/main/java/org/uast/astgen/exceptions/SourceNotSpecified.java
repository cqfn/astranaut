/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Source file not specified".
 *
 * @since 1.0
 */
public final class SourceNotSpecified extends InterpreterException {
    /**
     * The instance.
     */
    public static final  InterpreterException INSTANCE = new SourceNotSpecified();

    /**
     * Constructor.
     */
    private SourceNotSpecified() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Missed the [--source] option, source file not specified";
    }
}
