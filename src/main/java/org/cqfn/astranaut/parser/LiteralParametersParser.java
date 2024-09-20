/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.exceptions.ExpectedNativeLiteral;
import org.cqfn.astranaut.exceptions.ExpectedOnlyOneEntity;
import org.cqfn.astranaut.exceptions.ExpectedThreeOrFourParameters;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Literal;
import org.cqfn.astranaut.scanner.Comma;
import org.cqfn.astranaut.scanner.NativeCode;
import org.cqfn.astranaut.scanner.TokenList;

/**
 * Parses string as a parameters list for the {@link Literal} class.
 *
 * @since 0.1.5
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
            LiteralParametersParser.checkSegment(segment);
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
