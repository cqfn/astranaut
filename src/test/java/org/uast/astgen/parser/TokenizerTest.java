/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.scanner.Scanner;

/**
 * Test for {@link Tokenizer} class.
 *
 * @since 1.0
 */
public class TokenizerTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "{aaa,[bbb123<\"ccc\">],(ddd)}";

    /**
     * Source string (incorrect).
     */
    private static final String INCORRECT = "example$123";

    /**
     * Test scanner with string literal.
     */
    @Test
    public void correctSequence() {
        final Tokenizer tokenizer = new Tokenizer(TokenizerTest.CORRECT);
        boolean oops = false;
        try {
            tokenizer.getTokens();
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
        final Scanner scanner = new Scanner(TokenizerTest.INCORRECT);
        boolean oops = false;
        try {
            scanner.getToken();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
