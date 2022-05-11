/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

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
