/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Only one list descriptor is allowed".
 *
 * @since 1.0
 */
public final class OnlyOneListDescriptor extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new OnlyOneListDescriptor();

    /**
     * Constructor.
     */
    private OnlyOneListDescriptor() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Only one list descriptor is allowed";
    }
}
