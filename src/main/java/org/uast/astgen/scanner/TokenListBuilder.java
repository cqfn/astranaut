/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

import java.util.LinkedList;
import java.util.List;

/**
 * The builder of list of tokens.
 *
 * @since 1.0
 */
public class TokenListBuilder {
    /**
     * The modifiable token list.
     */
    private final List<Token> list;

    /**
     * Constructor.
     */
    public TokenListBuilder() {
        this.list = new LinkedList<>();
    }

    /**
     * Adds a token to the builder.
     * @param token A token
     */
    public void addToken(final Token token) {
        this.list.add(token);
    }

    /**
     * Checks whether the builder is empty (contains no tokens).
     * @return The result of checking
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /**
     * Creates a list of tokens.
     * @return A list of tokens
     */
    public TokenList createList() {
        final Token[] array = new Token[this.list.size()];
        this.list.toArray(array);
        return new TokenList() {
            @Override
            public int size() {
                return array.length;
            }

            @Override
            public Token get(final int index) throws IndexOutOfBoundsException {
                return array[index];
            }
        };
    }
}
