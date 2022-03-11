/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link HoleMarker} class.
 *
 * @since 1.0
 */
public class HoleTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "  #123  ";

    /**
     * Output example.
     */
    private static final String EXPECTED = "#123";

    /**
     * Source string (incorrect).
     */
    private static final String INCORRECT = "#abc";

    /**
     * Test scanner with hole marker.
     */
    @Test
    public void correctHoleMarker() {
        final Scanner scanner = new Scanner(HoleTest.CORRECT);
        Token token = null;
        boolean oops = false;
        try {
            token = scanner.getToken();
            Assertions.assertInstanceOf(HoleMarker.class, token);
            Assertions.assertEquals(token.toString(), HoleTest.EXPECTED);
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
        final Scanner scanner = new Scanner(HoleTest.INCORRECT);
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
