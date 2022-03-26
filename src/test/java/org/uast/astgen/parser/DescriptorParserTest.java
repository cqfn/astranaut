/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.TokenList;

/**
 * Test for {@link DescriptorParser} class.
 *
 * @since 1.0
 */
public class DescriptorParserTest {
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
     * Runs the descriptor parser with specified source.
     * @param source Source code
     * @return List of parameters
     */
    private List<Parameter> run(final String source) {
        final Tokenizer tokenizer = new Tokenizer(source);
        List<Parameter> result = Collections.emptyList();
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final DescriptorParser parser = new DescriptorParser(tokens);
            result = parser.parseAsParameters();
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
     * Runs the descriptor parser with specified source
     * and expects only one parameter.
     * @param source Source code
     * @return A parameter
     */
    private Parameter extractOne(final String source) {
        final List<Parameter> list = this.run(source);
        Assertions.assertEquals(1, list.size());
        return list.get(0);
    }
}
