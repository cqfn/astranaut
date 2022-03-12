/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

/**
 * Token that represents closing round bracket.
 *
 * @since 1.0
 */
public final class ClosingRoundBracket extends Bracket {
    /**
     * The instance.
     */
    public static final Bracket INSTANCE = new ClosingRoundBracket();

    /**
     * Private constructor.
     */
    private ClosingRoundBracket() {
    }

    @Override
    public char getSymbol() {
        return ')';
    }

    @Override
    public char getPairSymbol() {
        return '(';
    }

    @Override
    public boolean isClosing() {
        return true;
    }
}
