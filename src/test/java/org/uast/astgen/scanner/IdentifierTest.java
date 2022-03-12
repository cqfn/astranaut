/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link Identifier} classes.
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
     * Test scanner with string contains two identifiers.
     */
    @Test
    public void twoIdentifiers() {
        final Scanner scanner = new Scanner(IdentifierTest.SOURCE);
        Token token = null;
        boolean oops = false;
        try {
            token = scanner.getToken();
            Assertions.assertInstanceOf(Identifier.class, token);
            token = scanner.getToken();
            Assertions.assertInstanceOf(Identifier.class, token);
            Assertions.assertEquals(token.toString(), IdentifierTest.EXPECTED);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
