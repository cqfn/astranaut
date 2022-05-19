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

package org.cqfn.astranaut.scanner;

import org.cqfn.astranaut.exceptions.ParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Scanner} and {@link HoleMarker} classes.
 *
 * @since 1.0
 */
public class HoleTest {
    /**
     * Source string (simple case, correct).
     */
    private static final String SIMPLE_CORRECT = "  #123  ";

    /**
     * Output example (simple case).
     */
    private static final String SIMPLE_EXPECTED = "#123";

    /**
     * Source string (simple case, incorrect).
     */
    private static final String SIMPLE_INCORRECT = "#abc";

    /**
     * Source string (with ellipsis, correct).
     */
    private static final String ELLIPSIS_CORRECT = "  #0...  ";

    /**
     * Output example (with ellipsis).
     */
    private static final String ELLIPSIS_EXPECTED = "#0...";

    /**
     * Source string (with ellipsis, incorrect).
     */
    private static final String WRONG_ELLIPSIS = "#0..,";

    /**
     * Test scanner with hole marker.
     */
    @Test
    public void correctHoleMarker() {
        final Scanner scanner = new Scanner(HoleTest.SIMPLE_CORRECT);
        Token token = null;
        boolean oops = false;
        try {
            token = scanner.getToken();
            Assertions.assertInstanceOf(HoleMarker.class, token);
            Assertions.assertEquals(token.toString(), HoleTest.SIMPLE_EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test scanner with incorrect hole marker.
     */
    @Test
    public void incorrectHoleMarker() {
        final Scanner scanner = new Scanner(HoleTest.SIMPLE_INCORRECT);
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Test scanner with hole marker that contains ellipsis.
     */
    @Test
    public void correctHoleMarkerWithEllipsis() {
        final Scanner scanner = new Scanner(HoleTest.ELLIPSIS_CORRECT);
        Token token = null;
        boolean oops = false;
        try {
            token = scanner.getToken();
            Assertions.assertInstanceOf(HoleMarker.class, token);
            Assertions.assertEquals(token.toString(), HoleTest.ELLIPSIS_EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test scanner with incorrect hole marker that contains "bad" ellipsis.
     */
    @Test
    public void incorrectHoleMarkerWithEllipsis() {
        final Scanner scanner = new Scanner(HoleTest.WRONG_ELLIPSIS);
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
