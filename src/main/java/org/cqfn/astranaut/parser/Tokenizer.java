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
package org.cqfn.astranaut.parser;

import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.scanner.Null;
import org.cqfn.astranaut.scanner.Scanner;
import org.cqfn.astranaut.scanner.Token;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.scanner.TokenListBuilder;

/**
 * Class that transform string into list of tokens.
 *
 * @since 1.0
 */
public class Tokenizer {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source Source string.
     */
    public Tokenizer(final String source) {
        this.source = source;
    }

    /**
     * Returns a list of tokens as a result of parsing.
     * @return A list of tokens
     * @throws ParserException Any exception thrown by the parser
     */
    public TokenList getTokens() throws ParserException {
        final TokenListBuilder result = new TokenListBuilder();
        final Scanner scanner = new Scanner(this.source);
        Token token = scanner.getToken();
        while (!(token instanceof Null)) {
            result.addToken(token);
            token = scanner.getToken();
        }
        return result.createList();
    }
}
