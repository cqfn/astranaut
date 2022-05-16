/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astgen.scanner;

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
