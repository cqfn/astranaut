/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ExpectedThreeOrFourParameters;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Literal;

/**
 * Test for {@link NodeParser} class.
 *
 * @since 1.0
 */
public class LiteralParserTest {
    /**
     * Valid source code.
     */
    private static final String SOURCE =
        "IntegerLiteral <- $int$, $String.valueOf(#)$, $Integer.parseInt(#)$";

    /**
     * Test case: integer literal.
     */
    @Test
    public void integerLiteral() {
        final boolean result = this.run(LiteralParserTest.SOURCE);
        Assertions.assertTrue(result);
    }

    /**
     * Test case: integer literal with exception.
     */
    @Test
    public void integerLiteralWithException() {
        final boolean result =
            this.run(LiteralParserTest.SOURCE.concat(", $NumberFormatException$"));
        Assertions.assertTrue(result);
    }

    /**
     * Test case: wrong declaration.
     */
    @Test
    public void wrongDeclaration() {
        final boolean result = this.run(
            "IntegerLiteral <- $int$",
            ExpectedThreeOrFourParameters.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Runs the literal parser.
     * @param source Source string
     * @return Test result ({@code true} means no exceptions)
     */
    private boolean run(final String source) {
        boolean oops = false;
        try {
            final Literal literal = new LiteralParser(source).parse();
            final String text = literal.toString();
            Assertions.assertEquals(source, text);
        } catch (final ParserException ignored) {
            oops = true;
        }
        return !oops;
    }

    /**
     * Runs the literal parser and expects an exception.
     * @param source Source string
     * @param type Error type
     * @param <T> Exception class
     * @return Test result ({@code true} means parser throw expected exception)
     */
    private <T> boolean run(final String source, final Class<T> type) {
        boolean oops = false;
        try {
            new LiteralParser(source).parse();
        } catch (final ParserException error) {
            Assertions.assertInstanceOf(type, error);
            oops = true;
        }
        return oops;
    }
}
