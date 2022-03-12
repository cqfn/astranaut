/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.scanner.TokenList;

/**
 * Test for {@link Tokenizer} class.
 *
 * @since 1.0
 */
public class TokenizerTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "{aaa,[bbb123<\"ccc\">],(#456)}";

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
            final TokenList tokens = tokenizer.getTokens();
            Assertions.assertEquals(tokens.toString(), TokenizerTest.CORRECT);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test scanner with incorrect string literal.
     */
    @Test
    public void incorrectSequence() {
        final Tokenizer tokenizer = new Tokenizer(TokenizerTest.INCORRECT);
        boolean oops = false;
        try {
            tokenizer.getTokens();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
