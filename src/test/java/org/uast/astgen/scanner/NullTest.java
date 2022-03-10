/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Scanner} and {@link Null} class.
 *
 * @since 1.0
 */
public class NullTest {
    /**
     * Argument example.
     */
    private static final String EXPECTED = "<null>";

    /**
     * Test passing an argument to main().
     */
    @Test
    public void emptyStringAsInput() {
        final Scanner scanner = new Scanner("");
        final Token token = scanner.getToken();
        Assertions.assertEquals(token.toString(), NullTest.EXPECTED);
    }
}
