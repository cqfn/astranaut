/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Destination file not specified".
 *
 * @since 1.0
 */
public final class DestinationNotSpecified extends InterpreterException {
    /**
     * The instance.
     */
    public static final  InterpreterException INSTANCE = new DestinationNotSpecified();

    /**
     * Constructor.
     */
    private DestinationNotSpecified() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Missed the [--destination] option, destination file not specified";
    }
}
