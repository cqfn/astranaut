/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

/**
 * Token that represents opening angle bracket.
 *
 * @since 1.0
 */
public final class OpeningAngleBracket extends Bracket {
    /**
     * The instance.
     */
    public static final Bracket INSTANCE = new OpeningAngleBracket();

    /**
     * Private constructor.
     */
    private OpeningAngleBracket() {
    }

    @Override
    public char getSymbol() {
        return '<';
    }

    @Override
    public char getPairSymbol() {
        return '>';
    }

    @Override
    public boolean isClosing() {
        return false;
    }
}
