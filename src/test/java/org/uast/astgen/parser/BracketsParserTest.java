/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.BracketsDoNotMatch;
import org.uast.astgen.exceptions.NotClosedBracket;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.scanner.CurlyBracketsPair;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;

/**
 * Test for {@link BracketsParser} class.
 *
 * @since 1.0
 */
public class BracketsParserTest {
    /**
     * Source string (correct).
     */
    private static final String CORRECT = "{aaa,[bbb123<\"ccc\">],(#456)}";

    /**
     * Source string (incorrect).
     */
    private static final String UNCLOSED = "aaa{bbb[ccc]";

    /**
     * Another source string (incorrect).
     */
    private static final String MISMATCH = "aaa{bbb[ccc}";

    /**
     * Test the bracket parser with correct sequence.
     */
    @Test
    public void correctSequence() {
        final Tokenizer tokenizer = new Tokenizer(BracketsParserTest.CORRECT);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final BracketsParser parser = new BracketsParser(tokens);
            final TokenList parsed = parser.parse();
            Assertions.assertEquals(tokens.toString(), BracketsParserTest.CORRECT);
            Assertions.assertEquals(parsed.size(), 1);
            final Token token = parsed.get(0);
            Assertions.assertInstanceOf(CurlyBracketsPair.class, token);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test the bracket parser with incorrect sequence (not closed bracket).
     */
    @Test
    public void notClosedBracket() {
        final Tokenizer tokenizer = new Tokenizer(BracketsParserTest.UNCLOSED);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final BracketsParser parser = new BracketsParser(tokens);
            parser.parse();
        } catch (final ParserException exception) {
            Assertions.assertInstanceOf(NotClosedBracket.class, exception);
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Test the bracket parser with incorrect sequence
     * (opening bracket does not match closing).
     */
    @Test
    public void bracketsMismatch() {
        final Tokenizer tokenizer = new Tokenizer(BracketsParserTest.MISMATCH);
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final BracketsParser parser = new BracketsParser(tokens);
            parser.parse();
        } catch (final ParserException exception) {
            Assertions.assertInstanceOf(BracketsDoNotMatch.class, exception);
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
