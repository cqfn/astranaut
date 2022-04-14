/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.EmptyDataLiteral;
import org.uast.astgen.exceptions.ExpectedData;
import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.TokenList;

/**
 * Test for {@link ParametersListParser} class.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ParametersListParserTest {
    /**
     * Test string contains one hole.
     */
    @Test
    public void hole() {
        final Parameter parameter = this.extractOne("#1");
        Assertions.assertInstanceOf(Hole.class, parameter);
    }

    /**
     * Test string contains simple name.
     */
    @Test
    public void simpleName() {
        final Parameter parameter = this.extractOne("Expression");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains tagged name.
     */
    @Test
    public void taggedName() {
        final Parameter parameter = this.extractOne("left@Expression");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains hole as a data.
     */
    @Test
    public void holeAsData() {
        final Parameter parameter = this.extractOne("literal<#1>");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains string literal as a data.
     */
    @Test
    public void stringData() {
        final Parameter parameter = this.extractOne("literal<\"+\">");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains empty data descriptor.
     */
    @Test
    public void emptyData() {
        final String message = this.expectError("Test<>", EmptyDataLiteral.class);
        Assertions.assertEquals("Empty data literal: 'Test<>'", message);
    }

    /**
     * Test string contains data descriptor with two entities.
     */
    @Test
    public void twoDataEntities() {
        final String message = this.expectError("Test<#1,#2>", ExpectedOnlyOneEntity.class);
        Assertions.assertEquals("Expected only one entity: 'Test<#1,#2>'", message);
    }

    /**
     * Test string contains bad data descriptor.
     */
    @Test
    public void badData() {
        final String message = this.expectError("Test<ABC>", ExpectedData.class);
        Assertions.assertEquals(
            "Expected a data: 'Test<#...>' or 'Test<\"...\">'",
            message
        );
    }

    /**
     * Test string contains descriptor with two parameters.
     */
    @Test
    public void twoParameters() {
        final Parameter parameter = this.extractOne("Addition(Expression, Expression)");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains descriptor with three parameters.
     */
    @Test
    public void threeParameters() {
        final Parameter parameter = this.extractOne(
            "simpleExpression(#1, literal<\"+\">, #2)"
        );
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains optional descriptor.
     */
    @Test
    public void optional() {
        final Parameter parameter = this.extractOne("[Expression]");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Test string contains descriptor that is a list.
     */
    @Test
    public void list() {
        final Parameter parameter = this.extractOne("{Expression}");
        Assertions.assertInstanceOf(Descriptor.class, parameter);
    }

    /**
     * Runs the descriptor parser with specified source.
     * @param source Source code
     * @return List of parameters
     */
    private List<Parameter> run(final String source) {
        final Tokenizer tokenizer = new Tokenizer(source);
        List<Parameter> result = Collections.emptyList();
        boolean oops = false;
        try {
            TokenList tokens = tokenizer.getTokens();
            tokens = new BracketsParser(tokens).parse();
            final ParametersListParser parser = new ParametersListParser(
                tokens,
                new LabelFactory()
            );
            result = parser.parse();
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final StringBuilder builder = new StringBuilder();
        boolean flag = false;
        for (final Parameter parameter : result) {
            if (flag) {
                builder.append(", ");
            }
            flag = true;
            builder.append(parameter.toString());
        }
        Assertions.assertEquals(source, builder.toString());
        return result;
    }

    /**
     * Runs the descriptor parser with specified source and expects only one parameter.
     * @param source Source code
     * @return A parameter
     */
    private Parameter extractOne(final String source) {
        final List<Parameter> list = this.run(source);
        Assertions.assertEquals(1, list.size());
        return list.get(0);
    }

    /**
     * Runs the descriptor parser with specified source and expects an error.
     * @param source Source code
     * @param type Error type
     * @param <T> Exception class
     * @return Error message
     */
    private <T> String expectError(final String source, final Class<T> type) {
        final Tokenizer tokenizer = new Tokenizer(source);
        boolean oops = false;
        String message = "";
        try {
            TokenList tokens = tokenizer.getTokens();
            tokens = new BracketsParser(tokens).parse();
            final ParametersListParser parser = new ParametersListParser(
                tokens,
                new LabelFactory()
            );
            parser.parse();
        } catch (final ParserException error) {
            Assertions.assertInstanceOf(type, error);
            oops = true;
            message = error.getErrorMessage();
        }
        Assertions.assertTrue(oops);
        return message;
    }
}
