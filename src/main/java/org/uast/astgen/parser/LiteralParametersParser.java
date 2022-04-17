/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.ArrayList;
import java.util.List;
import org.uast.astgen.exceptions.ExpectedNativeLiteral;
import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.exceptions.ExpectedThreeOrFourParameters;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Literal;
import org.uast.astgen.scanner.Comma;
import org.uast.astgen.scanner.NativeCode;
import org.uast.astgen.scanner.TokenList;

/**
 * Parses string as a parameters list for the {@link Literal} class.
 *
 * @since 1.0
 */
public class LiteralParametersParser {
    /**
     * Required parameters number.
     */
    private static final int MIN_COUNT = 3;

    /**
     * Maximum parameters number.
     */
    private static final int MAX_COUNT = 4;

    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source Source string
     */
    public LiteralParametersParser(final String source) {
        this.source = source;
    }

    /**
     * Parses source string as a parameters list.
     * @return A list
     * @throws ParserException If the source string can't be parsed as a parameters list
     */
    public List<String> parse() throws ParserException {
        final TokenList tokens = new Tokenizer(this.source).getTokens();
        final TokenList[] segments = new Splitter(tokens)
            .split(token -> token instanceof Comma);
        if (segments.length < LiteralParametersParser.MIN_COUNT
            || segments.length > LiteralParametersParser.MAX_COUNT) {
            throw ExpectedThreeOrFourParameters.INSTANCE;
        }
        final List<String> result = new ArrayList<>(segments.length);
        for (final TokenList segment : segments) {
            this.checkSegment(segment);
            final NativeCode token = (NativeCode) segment.get(0);
            result.add(token.getCode());
        }
        return result;
    }

    /**
     * Checks the tokens sequence (should be only one {@link NativeCode} token.
     * @param segment The segment
     * @throws ParserException If the checking failed
     */
    private static void checkSegment(final TokenList segment) throws ParserException {
        if (segment.size() > 1) {
            throw new ExpectedOnlyOneEntity("$code$");
        }
        if (segment.size() == 0 || !(segment.get(0) instanceof NativeCode)) {
            throw ExpectedNativeLiteral.INSTANCE;
        }
    }
}
