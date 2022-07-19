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
import org.cqfn.astranaut.scanner.Comma;
import org.cqfn.astranaut.scanner.TokenList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Splitter} class.
 *
 * @since 0.1.5
 */
public class SplitterTest {
    /**
     * Source string.
     */
    private static final String SOURCE = "aaa bbb, ccc ddd eee";

    /**
     * Expected list size.
     */
    private static final int SIZE = 3;

    /**
     * Expected token value.
     */
    private static final String VALUE = "ddd";

    /**
     * Test the tokenizer with correct string.
     */
    @Test
    public void split() {
        final Tokenizer tokenizer = new Tokenizer(SplitterTest.SOURCE);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final Splitter splitter = new Splitter(tokens);
            final TokenList[] result = splitter.split(token -> token instanceof Comma);
            Assertions.assertEquals(result[1].size(), SplitterTest.SIZE);
            Assertions.assertEquals(result[1].get(1).toString(), SplitterTest.VALUE);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
