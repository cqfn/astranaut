/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ExpectedIdentifierAfterAt;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.AtSign;
import org.uast.astgen.scanner.Comma;
import org.uast.astgen.scanner.HoleMarker;
import org.uast.astgen.scanner.Identifier;
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
}
