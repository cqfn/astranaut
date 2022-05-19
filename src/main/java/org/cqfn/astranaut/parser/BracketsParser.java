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

import java.util.Iterator;
import org.cqfn.astranaut.exceptions.BracketsDoNotMatch;
import org.cqfn.astranaut.exceptions.NotClosedBracket;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.scanner.Bracket;
import org.cqfn.astranaut.scanner.Token;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.scanner.TokenListBuilder;

/**
 * Combines brackets and also tokens inside brackets into non-terminals.
 *
 * @since 1.0
 */
public class BracketsParser {
    /**
     * Source list.
     */
    private final TokenList source;

    /**
     * Constructor.
     * @param source Source tokens list.
     */
    public BracketsParser(final TokenList source) {
        this.source = source;
    }

    /**
     * Parses source token list.
     * @return A new tokens list, where brackets and also tokens inside brackets
     *  combined into non-terminals.
     * @throws ParserException Parser exception
     */
    public TokenList parse() throws ParserException {
        return this.parse(this.source.iterator(), '\0', '\0');
    }

    /**
     * Parsing (recursive method).
     * @param tokens Iterator by tokens
     * @param opening Opening bracket
     * @param closing Closing bracket
     * @return A new tokens list, where brackets and also tokens inside brackets
     *  combined into non-terminals.
     * @throws ParserException Parser exception
     */
    private TokenList parse(final Iterator<Token> tokens,
        final char opening,
        final char closing) throws ParserException {
        final TokenListBuilder result = new TokenListBuilder();
        boolean closed = false;
        while (tokens.hasNext()) {
            final Token token = tokens.next();
            if (!(token instanceof Bracket)) {
                result.addToken(token);
                continue;
            }
            final Bracket bracket = (Bracket) token;
            if (bracket.isClosing()) {
                if (closing != bracket.getSymbol()) {
                    throw new BracketsDoNotMatch(opening, closing);
                }
                closed = true;
                break;
            } else {
                final TokenList children = this.parse(
                    tokens,
                    bracket.getSymbol(),
                    bracket.getPairSymbol()
                );
                result.addToken(bracket.createPair(children));
            }
        }
        if (!closed && closing != '\0') {
            throw new NotClosedBracket(opening);
        }
        return result.createList();
    }
}
