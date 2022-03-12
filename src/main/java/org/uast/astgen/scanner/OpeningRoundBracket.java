/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

/**
 * Token that represents opening round bracket.
 *
 * @since 1.0
 */
public final class OpeningRoundBracket extends Bracket {
    /**
     * The instance.
     */
    public static final Bracket INSTANCE = new OpeningRoundBracket();

    /**
     * Private constructor.
     */
    private OpeningRoundBracket() {
    }

    @Override
    public char getSymbol() {
        return '(';
    }

    @Override
    public char getPairSymbol() {
        return ')';
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public BracketsPair createPair(final TokenList tokens) {
        return new RoundBracketsPair(tokens);
    }
}
