/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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
package org.cqfn.astranaut.scanner;

import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.DescriptorAttribute;

/**
 * Token that represents a pair of brackets and list of tokens between.
 *
 * @since 0.1.5
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

    /**
     * Returns suitable descriptor attribute.
     * @return Descriptor attribute.
     * @throws ParserException If token has no suitable descriptor attribute.
     */
    public abstract DescriptorAttribute getDescriptorAttribute() throws ParserException;

    @Override
    public final String toString() {
        return new StringBuilder()
            .append(this.getOpeningBracket())
            .append(this.tokens.toString())
            .append(this.getClosingBracket())
            .toString();
    }
}
