/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

/**
 * Token that represents a pair of brackets and list of tokens between.
 *
 * @since 1.0
 */
public abstract class BracketsPair implements Token {
    /**
     * The list of tokens.
     */
    private final TokenList tokens;

    /**
     * Constructor.
     * @param tokens The list of tokens.
     */
    public BracketsPair(final TokenList tokens) {
        this.tokens = tokens;
    }

    /**
     * Returns the list of tokens.
     * @return List of tokens
     */
    public final TokenList getTokens() {
        return this.tokens;
    }

    /**
     * Returns opening bracket.
     * @return A symbol
     */
    public abstract char getOpeningBracket();

    /**
     * Returns closing bracket.
     * @return A symbol
     */
    public abstract char getClosingBracket();

    @Override
    public final String toString() {
        return new StringBuilder()
            .append(this.getOpeningBracket())
            .append(this.tokens.toString())
            .append(this.getClosingBracket())
            .toString();
    }
}
