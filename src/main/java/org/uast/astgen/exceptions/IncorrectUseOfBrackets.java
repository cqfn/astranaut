/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Incorrect use of brackets".
 *
 * @since 1.0
 */
public final class IncorrectUseOfBrackets extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new IncorrectUseOfBrackets();

    /**
     * Constructor.
     */
    private IncorrectUseOfBrackets() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Incorrect use of brackets";
    }
}
