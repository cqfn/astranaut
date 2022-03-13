/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.Iterator;
import org.uast.astgen.exceptions.BracketsDoNotMatch;
import org.uast.astgen.exceptions.NotClosedBracket;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.scanner.Bracket;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.scanner.TokenListBuilder;

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
