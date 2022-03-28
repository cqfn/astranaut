/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;

/**
 * Test for {@link Scanner} and {@link Bracket} classes.
 *
 * @since 1.0
 */
public class BracketsTest {
    /**
     * Source code.
     */
    private static final String SOURCE = "{ one, [ two ], <t(h)ree> }";

    /**
     * Output example.
     */
    private static final String EXPECTED = "{[]<()>}";

    /**
     * Test scanner with string contains brackets.
     */
    @Test
    public void brackets() {
        final Scanner scanner = new Scanner(BracketsTest.SOURCE);
        final StringBuilder result = new StringBuilder();
        boolean oops = false;
        try {
            Token token = null;
            do {
                token = scanner.getToken();
                if (token instanceof Bracket) {
                    result.append(token.toString());
                }
            } while (!(token instanceof Null));
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(result.toString(), BracketsTest.EXPECTED);
    }
}
