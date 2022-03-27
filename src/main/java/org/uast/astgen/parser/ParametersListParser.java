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
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.BracketsPair;
import org.uast.astgen.scanner.Comma;
import org.uast.astgen.scanner.HoleMarker;
import org.uast.astgen.scanner.Identifier;
import org.uast.astgen.scanner.RoundBracketsPair;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;

/**
 * Extracts descriptors and descriptor parameters from list of tokens.
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
                result.add(ParametersListParser.parseDescriptor(segment, DescriptorAttribute.NONE));
            } else if (first instanceof BracketsPair) {
                result.add(ParametersListParser.parseOptional(segment));
            } else {
                throw new CantParseSequence(segment);
            }
        }
        return result;
    }

    /**
     * Parses list of tokens as a descriptor.
     * @param segment List of tokens
     * @param attribute Descriptor attribute (optional, list)
     * @return A descriptor
     * @throws ParserException If the token list cannot be parsed as a descriptor
     */
    private static Descriptor parseDescriptor(final TokenList segment,
        final DescriptorAttribute attribute) throws ParserException {
        final TokenStack stack = new TokenStack(segment.iterator());
        assert stack.hasTokens();
        final Token first = stack.pop();
        assert first instanceof Identifier;
        final String name = ((Identifier) first).getValue();
        final DescriptorFactory factory = new DescriptorFactory(name);
        factory.setAttribute(attribute);
        new TaggedNameParser(stack, factory).parse();
        ParametersListParser.parseParameters(stack, factory);
        new DataParser(stack, factory).parse();
        if (stack.hasTokens()) {
            throw new CantParseSequence(segment);
        }
        return factory.createDescriptor();
    }

    /**
     * Parses parameters inside descriptor.
     * @param stack Stack containing unused tokens
     * @param factory Descriptor factory
     * @throws ParserException If unused tokens cannot be converted to a data
     */
    private static void parseParameters(final TokenStack stack, final DescriptorFactory factory)
        throws ParserException {
        do {
            if (!stack.hasTokens()) {
                break;
            }
            final Token token = stack.pop();
            if (!(token instanceof RoundBracketsPair)) {
                stack.push(token);
                break;
            }
            final TokenList children = ((RoundBracketsPair) token).getTokens();
            final ParametersListParser parser = new ParametersListParser(children);
            final List<Parameter> parameters = parser.parse();
            factory.setParameters(parameters);
        } while (false);
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
        return parseDescriptor(children, attribute);
    }
}
