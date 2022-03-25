/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
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
        final Tokenizer tokenizer = new Tokenizer("#1");
        boolean oops = false;
        try {
            final TokenList tokens = tokenizer.getTokens();
            final DescriptorParser parser = new DescriptorParser(tokens);
            final List<Parameter> parameters = parser.parseAsParameters();
            Assertions.assertEquals(parameters.size(), 1);
            final Parameter parameter = parameters.get(0);
            Assertions.assertInstanceOf(Hole.class, parameter);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
