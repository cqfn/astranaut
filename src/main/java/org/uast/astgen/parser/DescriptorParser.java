/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.rules.Parameter;
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
                result.add(this.parseDescriptor(segment));
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
        final Iterator<Token> iterator = segment.iterator();
        assert iterator.hasNext();
        final Token first = iterator.next();
        assert first instanceof Identifier;
        final String name = ((Identifier) first).getValue();
        final DescriptorFactory factory = new DescriptorFactory(name);
        return factory.createDescriptor();
    }
}
