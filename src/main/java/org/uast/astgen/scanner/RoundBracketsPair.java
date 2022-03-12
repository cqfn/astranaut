/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

/**
 * Token that represents a pair of round brackets and list of tokens between.
 *
 * @since 1.0
 */
public class RoundBracketsPair extends BracketsPair {
    /**
     * Constructor.
     *
     * @param tokens The list of tokens.
     */
    public RoundBracketsPair(final TokenList tokens) {
        super(tokens);
    }

    @Override
    public final char getOpeningBracket() {
        return '(';
    }

    @Override
    public final char getClosingBracket() {
        return ')';
    }
}
