/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.exceptions.UnknownSymbol;
import org.uast.astgen.rules.Transformation;

/**
 * Test for {@link TransformationParser} class.
 *
 * @since 1.0
 */
public class TransformationParserTest {
    /**
     * Test case: simple transformation.
     */
    @Test
    public void simpleTransformation() {
        final boolean result = this.run("A -> B");
        Assertions.assertTrue(result);
    }

    /**
     * Test case: complex transformation.
     */
    @Test
    public void complexTransformation() {
        final boolean result = this.run(
            "singleExpression(identifier(literal<#13>)) -> Variable(#13)"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: more complex transformation.
     */
    @Test
    public void moreComplexTransformation() {
        final boolean result = this.run(
            "singleExpression(#1, literal<\"+\">, #2) -> Addition(#1, #2)"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: DSL line with exception.
     */
    @Test
    public void wrongSyntax() {
        final boolean result = this.run("A -> 123", UnknownSymbol.class);
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
            final Transformation rule = new TransformationParser(source).parse();
            final String text = rule.toString();
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
            new TransformationParser(source).parse();
        } catch (final ParserException error) {
            Assertions.assertInstanceOf(type, error);
            oops = true;
        }
        return oops;
    }
}
