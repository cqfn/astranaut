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

import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Scanner} class and tokens.
 * @since 1.0.0
 */
class ScannerTest {
    @Test
    void noTokens() {
        final Scanner scanner = new Scanner("   ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertNull(token);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void identifier() {
        final Scanner scanner = new Scanner(" Expression ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof Identifier);
            Assertions.assertEquals("Expression", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void comma() {
        final Scanner scanner = new Scanner(" , ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof Comma);
            Assertions.assertEquals(",", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void identifiersSeparatedByComma() {
        final Scanner scanner = new Scanner(" AssignableExpression, Expression ");
        boolean oops = false;
        try {
            Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof Identifier);
            token = scanner.getToken();
            Assertions.assertTrue(token instanceof Comma);
            token = scanner.getToken();
            Assertions.assertTrue(token instanceof Identifier);
            token = scanner.getToken();
            Assertions.assertNull(token);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void number() {
        final Scanner scanner = new Scanner("13 0");
        boolean oops = false;
        try {
            Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof Number);
            Assertions.assertEquals(13, ((Number) token).getValue());
            token = scanner.getToken();
            Assertions.assertTrue(token instanceof Number);
            Assertions.assertTrue(token instanceof Zero);
            Assertions.assertEquals(0, ((Zero) token).getValue());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void unknownSymbol() {
        final Scanner scanner = new Scanner(" ` ");
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Parser", exception.getInitiator());
            Assertions.assertEquals("Unknown symbol: '`'", exception.getErrorMessage());
        }
        Assertions.assertTrue(oops);
    }
}
