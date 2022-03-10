/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Scanner} and {@link Identifier} class.
 *
 * @since 1.0
 */
public class IdentifierTest {
    /**
     * Source string.
     */
    private static final String SOURCE = "first Second_2";

    /**
     * Output example.
     */
    private static final String EXPECTED = "Second_2";

    /**
     * Test passing an argument to main().
     */
    @Test
    public void twoIdentifiers() {
        final Scanner scanner = new Scanner(IdentifierTest.SOURCE);
        Token token = scanner.getToken();
        Assertions.assertInstanceOf(Identifier.class, token);
        token = scanner.getToken();
        Assertions.assertInstanceOf(Identifier.class, token);
        Assertions.assertEquals(token.toString(), IdentifierTest.EXPECTED);
    }
}
