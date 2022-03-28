/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link AtSign} classes.
 *
 * @since 1.0
 */
public class AtSignTest {
    /**
     * Source code.
     */
    private static final String SOURCE = "tag@Name";

    /**
     * Output example.
     */
    private static final String EXPECTED = "@";

    /**
     * Test scanner with string contains "at" sign.
     */
    @Test
    public void comma() {
        final Scanner scanner = new Scanner(AtSignTest.SOURCE);
        boolean oops = false;
        final Token token;
        try {
            scanner.getToken();
            token = scanner.getToken();
            Assertions.assertEquals(token.toString(), AtSignTest.EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
