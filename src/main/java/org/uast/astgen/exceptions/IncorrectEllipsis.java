/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Incorrect ellipsis format".
 *
 * @since 1.0
 */
public final class IncorrectEllipsis extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new IncorrectEllipsis();

    /**
     * Constructor.
     */
    private IncorrectEllipsis() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Incorrect ellipsis format, expected '...'";
    }
}
