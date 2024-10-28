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
@SuppressWarnings("PMD.TooManyMethods")
class ScannerTest {
    /**
     * Fake location of DSL code.
     */
    private static final Location LOCATION = new Location("test.dsl", 1, 1);

    @Test
    void noTokens() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, "   ");
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
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " Expression ");
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
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " , ");
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
    void atSymbol() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " @ ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof AtSymbol);
            Assertions.assertEquals("@", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void verticalLine() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " | ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof VerticalLine);
            Assertions.assertEquals("|", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void identifiersSeparatedByComma() {
        final Scanner scanner = new Scanner(
            ScannerTest.LOCATION,
            " AssignableExpression, Expression "
        );
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
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, "13 0");
        boolean oops = false;
        try {
            Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof Number);
            Assertions.assertEquals(13, ((Number) token).getValue());
            Assertions.assertEquals("13", token.toString());
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
    void openingCurlyBracket() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " { ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof OpeningCurlyBracket);
            Assertions.assertEquals("{", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void closingCurlyBracket() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " } ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof ClosingCurlyBracket);
            Assertions.assertEquals("}", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void openingSquareBracket() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " [ ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof OpeningSquareBracket);
            Assertions.assertEquals("[", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void closingSquareBracket() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " ] ");
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof ClosingSquareBracket);
            Assertions.assertEquals("]", token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void stringInSingleQuotes() {
        final String code = "'abc'";
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, code);
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof StringToken);
            Assertions.assertEquals("abc", ((StringToken) token).getValue());
            Assertions.assertEquals(code, token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void stringInDoubleQuotes() {
        final String code = "\"def\"";
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, code);
        boolean oops = false;
        try {
            final Token token = scanner.getToken();
            Assertions.assertTrue(token instanceof StringToken);
            Assertions.assertEquals("def", ((StringToken) token).getValue());
            Assertions.assertEquals(code, token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void stringWithEscapeSequences() {
        boolean oops = false;
        try {
            String code = "'abc\\nd\\re\\tf\\\\\\''";
            Scanner scanner = new Scanner(ScannerTest.LOCATION, code);
            Token token = scanner.getToken();
            Assertions.assertEquals(code, token.toString());
            code = "\"\\\"test\\\"\"";
            scanner = new Scanner(ScannerTest.LOCATION, code);
            token = scanner.getToken();
            Assertions.assertEquals(code, token.toString());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void unclosedSrtring() {
        Assertions.assertThrows(
            ParsingException.class,
            () -> new Scanner(ScannerTest.LOCATION, "'abc").getToken()
        );
    }

    @Test
    void invalidStringEscapeSequence() {
        Assertions.assertThrows(
            ParsingException.class,
            () -> new Scanner(ScannerTest.LOCATION, "'abc\\~'").getToken()
        );
    }

    @Test
    void unknownSymbol() {
        final Scanner scanner = new Scanner(ScannerTest.LOCATION, " ` ");
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Parser", exception.getInitiator());
            Assertions.assertEquals(
                "test.dsl, 1: Unknown symbol: '`'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }
}
