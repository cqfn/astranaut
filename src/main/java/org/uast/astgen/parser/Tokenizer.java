/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.ArrayList;
import java.util.List;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.scanner.Null;
import org.uast.astgen.scanner.Scanner;
import org.uast.astgen.scanner.Token;

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
    public List<Token> getTokens() throws ParserException {
        final List<Token> result = new ArrayList<>(16);
        final Scanner scanner = new Scanner(this.source);
        Token token = scanner.getToken();
        while (!(token instanceof Null)) {
            result.add(token);
            token = scanner.getToken();
        }
        return result;
    }
}
