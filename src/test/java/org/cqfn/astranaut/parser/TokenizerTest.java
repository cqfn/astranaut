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
import org.cqfn.astranaut.scanner.TokenList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Tokenizer} class.
 *
 * @since 0.1.5
 */
public class TokenizerTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "{aaa,[bbb123<\"ccc\">],(#456)}";

    /**
     * Source string (incorrect).
     */
    private static final String INCORRECT = "example^123";

    /**
     * Test the tokenizer with correct string.
     */
    @Test
    public void correctSequence() {
        final Tokenizer tokenizer = new Tokenizer(TokenizerTest.CORRECT);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            Assertions.assertEquals(tokens.toString(), TokenizerTest.CORRECT);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test the tokenizer with incorrect string.
     */
    @Test
    public void incorrectSequence() {
        final Tokenizer tokenizer = new Tokenizer(TokenizerTest.INCORRECT);
        boolean oops = false;
        try {
            tokenizer.getTokens();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
