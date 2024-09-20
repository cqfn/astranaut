/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cqfn.astranaut.parser;

import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.exceptions.CantParseSequence;
import org.cqfn.astranaut.exceptions.EmptyDataLiteral;
import org.cqfn.astranaut.exceptions.ExpectedComma;
import org.cqfn.astranaut.exceptions.ExpectedData;
import org.cqfn.astranaut.exceptions.ExpectedDescriptor;
import org.cqfn.astranaut.exceptions.ExpectedOnlyOneEntity;
import org.cqfn.astranaut.exceptions.IncorrectUseOfBrackets;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Hole;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link ParametersListParser} class.
 *
 * @since 0.1.5
 */
@SuppressWarnings("PMD.TooManyMethods")
class ParametersListParserTest {
    /**
     * Test string contains one hole.
     */
    @Test
    void hole() {
        final Parameter parameter = this.extractOne("#1");
        Assertions.assertTrue(parameter instanceof Hole);
    }

    /**
     * Test string contains simple name.
     */
    @Test
    void simpleName() {
        final Parameter parameter = this.extractOne("Expression");
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains optional descriptors not separated by a comma.
     */
    @Test
    void optionalNotSeparated() {
        final String message = this.expectError("[A] [B]", ExpectedComma.class);
        Assertions.assertEquals("Expected a comma after the token: '[A]'", message);
    }

    /**
     * Test string contains descriptors with incorrect brackets.
     */
    @Test
    void incorrectBrackets() {
        final String message = this.expectError("[A], <B>", IncorrectUseOfBrackets.class);
        Assertions.assertEquals("Incorrect use of brackets", message);
    }

    /**
     * Test string contains square brackets but without descriptor.
     */
    @Test
    void optionalDescriptorExpected() {
        final String message = this.expectError("[ ]", ExpectedDescriptor.class);
        Assertions.assertEquals("Expected a descriptor: '[...]'", message);
    }

    /**
     * Test string contains square brackets but without descriptor.
     */
    @Test
    void unknownTokenSequence() {
        final String message = this.expectError("Integer<\"1\"><\"2\">", CantParseSequence.class);
        Assertions.assertEquals("Can't parse tokens sequence: 'Integer<\"1\"><\"2\">'", message);
    }

    /**
     * Test string contains tagged name.
     */
    @Test
    void taggedName() {
        final Parameter parameter = this.extractOne("left@Expression");
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains hole as a data.
     */
    @Test
    void holeAsData() {
        final Parameter parameter = this.extractOne("literal<#1>");
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains string literal as a data.
     */
    @Test
    void stringData() {
        final Parameter parameter = this.extractOne("literal<\"+\">");
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains empty data descriptor.
     */
    @Test
    void emptyData() {
        final String message = this.expectError("Test<>", EmptyDataLiteral.class);
        Assertions.assertEquals("Empty data literal: 'Test<>'", message);
    }

    /**
     * Test string contains data descriptor with two entities.
     */
    @Test
    void twoDataEntities() {
        final String message = this.expectError("Test<#1,#2>", ExpectedOnlyOneEntity.class);
        Assertions.assertEquals("Expected only one entity: 'Test<#1,#2>'", message);
    }

    /**
     * Test string contains bad data descriptor.
     */
    @Test
    void badData() {
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
    void twoParameters() {
        final Parameter parameter = this.extractOne("Addition(Expression, Expression)");
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains descriptor with three parameters.
     */
    @Test
    void threeParameters() {
        final Parameter parameter = this.extractOne(
            "simpleExpression(#1, literal<\"+\">, #2)"
        );
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains optional descriptor.
     */
    @Test
    void optional() {
        final Parameter parameter = this.extractOne("[Expression]");
        Assertions.assertTrue(parameter instanceof Descriptor);
    }

    /**
     * Test string contains descriptor that is a list.
     */
    @Test
    void list() {
        final Parameter parameter = this.extractOne("{Expression}");
        Assertions.assertTrue(parameter instanceof Descriptor);
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
            final boolean result = type.isInstance(error);
            Assertions.assertTrue(result);
            oops = true;
            message = error.getErrorMessage();
        }
        Assertions.assertTrue(oops);
        return message;
    }
}
