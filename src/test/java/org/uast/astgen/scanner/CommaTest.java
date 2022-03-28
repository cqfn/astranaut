/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link Comma} classes.
 *
 * @since 1.0
 */
public class CommaTest {
    /**
     * Source code.
     */
    private static final String SOURCE = "one, two, three";

    /**
     * Output example.
     */
    private static final String EXPECTED = ",";

    /**
     * Test scanner with string contains a comma.
     */
    @Test
    public void comma() {
        final Scanner scanner = new Scanner(CommaTest.SOURCE);
        boolean oops = false;
        final Token token;
        try {
            scanner.getToken();
            token = scanner.getToken();
            Assertions.assertEquals(token.toString(), CommaTest.EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
