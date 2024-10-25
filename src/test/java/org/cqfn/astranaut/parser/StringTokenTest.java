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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link StringToken} class.
 * @since 1.0.0
 */
class StringTokenTest {
    /**
     * Test string.
     */
    private static final String TEST_STRING = "abc\rd\ne\tf\\'\"gh";

    @Test
    void simpleString() {
        final StringToken token = new StringToken('\'', "abc");
        Assertions.assertEquals("'abc'", token.toString());
    }

    @Test
    void singleQuotes() {
        final StringToken token = new StringToken('\'', StringTokenTest.TEST_STRING);
        Assertions.assertEquals(StringTokenTest.TEST_STRING, token.getValue());
        Assertions.assertEquals("'abc\\rd\\ne\\tf\\\\\\'\"gh'", token.toString());
    }

    @Test
    void doubleQuotes() {
        final StringToken token = new StringToken('\"', StringTokenTest.TEST_STRING);
        Assertions.assertEquals(StringTokenTest.TEST_STRING, token.getValue());
        Assertions.assertEquals("\"abc\\rd\\ne\\tf\\\\'\\\"gh\"", token.toString());
    }

    @Test
    void badQuote() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new StringToken('x', "X")
        );
    }
}
