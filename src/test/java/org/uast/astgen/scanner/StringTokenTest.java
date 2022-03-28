/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link StringToken} classes.
 *
 * @since 1.0
 */
public class StringTokenTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "  \"test \\n 123\"";

    /**
     * Output example.
     */
    private static final String EXPECTED = "\"test \\n 123\"";

    /**
     * Source string (incorrect).
     */
    private static final String INCORRECT = "  \"test";

    /**
     * Test scanner with string literal.
     */
    @Test
    public void correctString() {
        final Scanner scanner = new Scanner(StringTokenTest.CORRECT);
        Token token = null;
        boolean oops = false;
        try {
            token = scanner.getToken();
            Assertions.assertInstanceOf(StringToken.class, token);
            Assertions.assertEquals(token.toString(), StringTokenTest.EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test scanner with incorrect string literal.
     */
    @Test
    public void incorrectString() {
        final Scanner scanner = new Scanner(StringTokenTest.INCORRECT);
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
