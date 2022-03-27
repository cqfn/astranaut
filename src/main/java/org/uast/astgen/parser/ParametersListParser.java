/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.CantParseSequence;
import org.uast.astgen.exceptions.ExpectedComma;
import org.uast.astgen.exceptions.ExpectedDescriptor;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.BracketsPair;
import org.uast.astgen.scanner.Comma;
import org.uast.astgen.scanner.HoleMarker;
import org.uast.astgen.scanner.Identifier;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;

/**
 * Extracts parameters (holes and descriptors) from list of tokens.
 *
 * @since 1.0
 */
public class ParametersListParser {
    /**
     * The list of tokens.
     */
    private final TokenList tokens;

    /**
     * Constructor.
     * @param tokens The list of tokens
     */
    public ParametersListParser(final TokenList tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses list of tokens as a list of parameters.
     * @return List of parameters (can be empty)
     * @throws ParserException If the token list cannot be parsed as a list
     *  of parameters
     */
    public List<Parameter> parse() throws ParserException {
        final List<Parameter> result = new LinkedList<>();
        for (final TokenList segment
            : new Splitter(this.tokens).split(token -> token instanceof Comma)) {
            if (segment.size() == 0) {
                continue;
            }
            final Token first = segment.get(0);
            if (first instanceof HoleMarker) {
                result.add(((HoleMarker) first).createHole());
            } else if (first instanceof Identifier) {
                result.add(new DescriptorParser(segment).parse(DescriptorAttribute.NONE));
            } else if (first instanceof BracketsPair) {
                result.add(ParametersListParser.parseOptional(segment));
            } else {
                throw new CantParseSequence(segment);
            }
        }
        return result;
    }

    /**
     * Parses list of tokens as an optional or list descriptor.
     * @param segment List of tokens
     * @return A descriptor
     * @throws ParserException If the token list cannot be parsed as a descriptor
     */
    private static Descriptor parseOptional(final TokenList segment) throws ParserException {
        assert segment.size() > 0;
        final Token token = segment.get(0);
        if (segment.size() > 1) {
            throw new ExpectedComma(token);
        }
        assert token instanceof BracketsPair;
        final BracketsPair brackets = (BracketsPair) token;
        final TokenList children = brackets.getTokens();
        if (children.size() == 0) {
            final StringBuilder builder = new StringBuilder();
            builder.append(brackets.getOpeningBracket())
                .append("...")
                .append(brackets.getClosingBracket());
            throw new ExpectedDescriptor(builder.toString());
        }
        final DescriptorAttribute attribute = brackets.getDescriptorAttribute();
        return new DescriptorParser(children).parse(attribute);
    }
}
