/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link Null} class.
 *
 * @since 1.0
 */
public class NullTest {
    /**
     * Output example.
     */
    private static final String EXPECTED = "<null>";

    /**
     * Test scanner with empty string as input.
     */
    @Test
    public void emptyStringAsInput() {
        final Scanner scanner = new Scanner("");
        boolean oops = false;
        final Token token;
        try {
            token = scanner.getToken();
            Assertions.assertEquals(token.toString(), NullTest.EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
