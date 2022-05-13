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
