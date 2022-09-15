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

import org.cqfn.astranaut.exceptions.ExpectedNativeLiteral;
import org.cqfn.astranaut.exceptions.ExpectedThreeOrFourParameters;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Literal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LiteralParser} class.
 *
 * @since 0.1.5
 */
class LiteralParserTest {
    /**
     * Valid source code.
     */
    private static final String SOURCE =
        "IntegerLiteral <- $int$, $String.valueOf(#)$, $Integer.parseInt(#)$";

    /**
     * Test case: integer literal.
     */
    @Test
    void integerLiteral() {
        final boolean result = this.run(LiteralParserTest.SOURCE);
        Assertions.assertTrue(result);
    }

    /**
     * Test case: integer literal with exception.
     */
    @Test
    void integerLiteralWithException() {
        final boolean result =
            this.run(LiteralParserTest.SOURCE.concat(", $NumberFormatException$"));
        Assertions.assertTrue(result);
    }

    /**
     * Test case: integer literal with unexpected token.
     */
    @Test
    void nativeLiteralException() {
        final boolean result = this.run(
            LiteralParserTest.SOURCE.concat(", String"),
            ExpectedNativeLiteral.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: wrong declaration.
     */
    @Test
    void wrongDeclaration() {
        final boolean result = this.run(
            "IntegerLiteral <- $int$",
            ExpectedThreeOrFourParameters.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Runs the literal parser.
     * @param source Source string
     * @return Test result ({@code true} means no exceptions)
     */
    private boolean run(final String source) {
        boolean oops = false;
        try {
            final Literal literal = new LiteralParser(source).parse();
            final String text = literal.toString();
            Assertions.assertEquals(source, text);
        } catch (final ParserException ignored) {
            oops = true;
        }
        return !oops;
    }

    /**
     * Runs the literal parser and expects an exception.
     * @param source Source string
     * @param type Error type
     * @param <T> Exception class
     * @return Test result ({@code true} means parser throw expected exception)
     */
    private <T> boolean run(final String source, final Class<T> type) {
        boolean oops = false;
        try {
            new LiteralParser(source).parse();
        } catch (final ParserException error) {
            final boolean result = type.isInstance(error);
            Assertions.assertTrue(result);
            oops = true;
        }
        return oops;
    }
}
