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

import org.cqfn.astranaut.exceptions.BracketsDoNotMatch;
import org.cqfn.astranaut.exceptions.NotClosedBracket;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.scanner.CurlyBracketsPair;
import org.cqfn.astranaut.scanner.Token;
import org.cqfn.astranaut.scanner.TokenList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link BracketsParser} class.
 *
 * @since 0.1.5
 */
public class BracketsParserTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "{aaa,[bbb123<\"ccc\">],(#456)}";

    /**
     * Source string (incorrect).
     */
    private static final String UNCLOSED = "aaa{bbb[ccc]";

    /**
     * Another source string (incorrect).
     */
    private static final String MISMATCH = "aaa{bbb[ccc}";

    /**
     * Test the bracket parser with correct sequence.
     */
    @Test
    public void correctSequence() {
        final Tokenizer tokenizer = new Tokenizer(BracketsParserTest.CORRECT);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final BracketsParser parser = new BracketsParser(tokens);
            final TokenList parsed = parser.parse();
            Assertions.assertEquals(tokens.toString(), BracketsParserTest.CORRECT);
            Assertions.assertEquals(parsed.size(), 1);
            final Token token = parsed.get(0);
            Assertions.assertTrue(token instanceof CurlyBracketsPair);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test the bracket parser with incorrect sequence (not closed bracket).
     */
    @Test
    public void notClosedBracket() {
        final Tokenizer tokenizer = new Tokenizer(BracketsParserTest.UNCLOSED);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final BracketsParser parser = new BracketsParser(tokens);
            parser.parse();
        } catch (final ParserException exception) {
            oops = NotClosedBracket.class.isInstance(exception);
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Test the bracket parser with incorrect sequence
     * (opening bracket does not match closing).
     */
    @Test
    public void bracketsMismatch() {
        final Tokenizer tokenizer = new Tokenizer(BracketsParserTest.MISMATCH);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final BracketsParser parser = new BracketsParser(tokens);
            parser.parse();
        } catch (final ParserException exception) {
            oops = BracketsDoNotMatch.class.isInstance(exception);
        }
        Assertions.assertTrue(oops);
    }
}
