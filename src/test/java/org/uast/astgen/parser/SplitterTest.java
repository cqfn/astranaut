/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.scanner.Comma;
import org.uast.astgen.scanner.TokenList;

/**
 * Test for {@link Splitter} class.
 *
 * @since 1.0
 */
public class SplitterTest {
    /**
     * Source string.
     */
    private static final String SOURCE = "aaa bbb, ccc ddd eee";

    /**
     * Expected list size.
     */
    private static final int SIZE = 3;

    /**
     * Expected token value.
     */
    private static final String VALUE = "ddd";

    /**
     * Test the tokenizer with correct string.
     */
    @Test
    public void split() {
        final Tokenizer tokenizer = new Tokenizer(SplitterTest.SOURCE);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final Splitter splitter = new Splitter(tokens);
            final TokenList[] result = splitter.split(token -> token instanceof Comma);
            Assertions.assertEquals(result[1].size(), SplitterTest.SIZE);
            Assertions.assertEquals(result[1].get(1).toString(), SplitterTest.VALUE);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
