/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

/**
 * Exception "Bad rule syntax".
 *
 * @since 1.0
 */
public final class BadRuleSyntax extends ParserException {
    /**
     * The instance.
     */
    public static final  ParserException INSTANCE = new BadRuleSyntax();

    /**
     * Constructor.
     */
    private BadRuleSyntax() {
        super();
    }

    @Override
    public String getErrorMessage() {
        return "Bad rule syntax";
    }
}
