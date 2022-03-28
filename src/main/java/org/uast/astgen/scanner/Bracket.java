/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

/**
 * Token that represents bracket.
 *
 * @since 1.0
 */
public abstract class Bracket implements Token {
    @Override
    public final String toString() {
        return String.valueOf(this.getSymbol());
    }

    /**
     * Returns the symbol of the bracket.
     * @return A symbol
     */
    public abstract char getSymbol();

    /**
     * Returns the pair symbol of the bracket.
     * @return A symbol
     */
    public abstract char getPairSymbol();

    /**
     * Returns flag, whether the bracket is closing.
     * @return The flag
     */
    public abstract boolean isClosing();

    /**
     * Creates corresponding {@link BracketsPair} instance.
     * @param tokens The list of tokens inside brackets pair.
     * @return A token
     */
    public abstract BracketsPair createPair(TokenList tokens);
}
