/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.EmptyDataLiteral;
import org.uast.astgen.exceptions.ExpectedData;
import org.uast.astgen.exceptions.ExpectedIdentifierAfterAt;
import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.AngleBracketsPair;
import org.uast.astgen.scanner.AtSign;
import org.uast.astgen.scanner.Comma;
import org.uast.astgen.scanner.HoleMarker;
import org.uast.astgen.scanner.Identifier;
import org.uast.astgen.scanner.StringToken;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;

/**
 * Extracts descriptors and descriptor parameters from list of tokens.
 *
 * @since 1.0
 */
public class DescriptorParser {
    /**
     * The list of tokens.
     */
    private final TokenList tokens;

    /**
     * Constructor.
     * @param tokens The list of tokens
     */
    public DescriptorParser(final TokenList tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses list of tokens as a list of parameters.
     * @return List of parameters (can be empty)
     * @throws ParserException If the token list cannot be parsed as a list
     *  of parameters
     */
    public List<Parameter> parseAsParameters() throws ParserException {
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
                result.add(DescriptorParser.parseDescriptor(segment));
            }
        }
        return result;
    }

    /**
     * Parses list of tokens as a descriptor.
     * @param segment List of tokens
     * @return A descriptor
     * @throws ParserException If the token list cannot be parsed as a descriptor
     */
    private static Descriptor parseDescriptor(final TokenList segment) throws ParserException {
        final TokenStack stack = new TokenStack(segment.iterator());
        assert stack.hasTokens();
        final Token first = stack.pop();
        assert first instanceof Identifier;
        final String name = ((Identifier) first).getValue();
        final DescriptorFactory factory = new DescriptorFactory(name);
        DescriptorParser.parseTaggedName(stack, factory);
        DescriptorParser.parseData(stack, factory);
        return factory.createDescriptor();
    }

    /**
     * Parses tagged name.
     * @param stack Stack containing unused tokens
     * @param factory Descriptor factory
     * @throws ParserException If unused tokens cannot be converted to a tagged name
     */
    private static void parseTaggedName(final TokenStack stack, final DescriptorFactory factory)
        throws ParserException {
        do {
            if (!stack.hasTokens()) {
                break;
            }
            Token token = stack.pop();
            if (!(token instanceof AtSign)) {
                stack.push(token);
                break;
            }
            if (!stack.hasTokens()) {
                throw ExpectedIdentifierAfterAt.INSTANCE;
            }
            token = stack.pop();
            if (!(token instanceof Identifier)) {
                throw ExpectedIdentifierAfterAt.INSTANCE;
            }
            factory.replaceName(((Identifier) token).getValue());
        } while (false);
    }

    /**
     * Parses data inside descriptor.
     * @param stack Stack containing unused tokens
     * @param factory Descriptor factory
     * @throws ParserException If unused tokens cannot be converted to a data
     */
    private static void parseData(final TokenStack stack, final DescriptorFactory factory)
        throws ParserException {
        do {
            if (!stack.hasTokens()) {
                break;
            }
            final Token token = stack.pop();
            if (!(token instanceof AngleBracketsPair)) {
                stack.push(token);
                break;
            }
            final TokenList children = ((AngleBracketsPair) token).getTokens();
            DescriptorParser.extractData(children, factory);
        } while (false);
    }

    /**
     * Extracts data from the list of tokens.
     * @param tokens The list of tokens
     * @param factory Descriptor factory
     * @throws ParserException If unused tokens cannot be converted to a data
     */
    private static void extractData(final TokenList tokens, final DescriptorFactory factory)
        throws ParserException {
        final int count = tokens.size();
        if (count == 0) {
            throw new EmptyDataLiteral(factory.createDescriptor().getFullName());
        }
        if (count > 1) {
            final StringBuilder builder = new StringBuilder();
            builder.append(factory.createDescriptor().getFullName())
                .append('<')
                .append(tokens.toString())
                .append('>');
            throw new ExpectedOnlyOneEntity(builder.toString());
        }
        final Token child = tokens.get(0);
        if (child instanceof HoleMarker) {
            factory.setData(((HoleMarker) child).createHole());
        } else if (child instanceof StringToken) {
            factory.setData(((StringToken) child).createStringData());
        } else {
            throw new ExpectedData(factory.createDescriptor().getFullName());
        }
    }
}
