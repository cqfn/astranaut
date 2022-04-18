/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.uast.astgen.exceptions.BadRuleSyntax;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.rules.Transformation;
import org.uast.astgen.scanner.TokenList;

/**
 * Parser of {@link Transformation} rules.
 *
 * @since 1.0
 */
public class TransformationParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Label factory.
     */
    private final LabelFactory labels;

    /**
     * Constructor.
     * @param source Source string
     */
    public TransformationParser(final String source) {
        this.source = source;
        this.labels = new LabelFactory();
    }

    /**
     * Parses source string as a {@link Transformation} descriptor.
     * @return A node descriptor
     * @throws ParserException If the source string can't be parsed as a transformation descriptor
     */
    public Transformation parse() throws ParserException {
        assert this.source.contains("->");
        final String[] pair = this.source.split("->");
        if (pair.length > 2) {
            throw BadRuleSyntax.INSTANCE;
        }
        return new Transformation(
            this.parsePart(pair[0]),
            this.parsePart(pair[1])
        );
    }

    /**
     * Parses a part (left or right) of the rule.
     * @param code DSL code
     * @return A descriptor
     * @throws ParserException If the source string can't be parsed as a descriptor
     */
    private Descriptor parsePart(final String code) throws ParserException {
        TokenList tokens = new Tokenizer(code).getTokens();
        tokens = new BracketsParser(tokens).parse();
        return new DescriptorParser(tokens, this.labels).parse(DescriptorAttribute.NONE);
    }
}
