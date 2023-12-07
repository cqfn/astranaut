/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.exceptions.CantParseSequence;
import org.cqfn.astranaut.exceptions.ExpectedComma;
import org.cqfn.astranaut.exceptions.ExpectedDescriptor;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Empty;
import org.cqfn.astranaut.rules.Extension;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.scanner.Ampersand;
import org.cqfn.astranaut.scanner.BracketsPair;
import org.cqfn.astranaut.scanner.Comma;
import org.cqfn.astranaut.scanner.EmptySymbol;
import org.cqfn.astranaut.scanner.HoleMarker;
import org.cqfn.astranaut.scanner.Identifier;
import org.cqfn.astranaut.scanner.Token;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;

/**
 * Extracts parameters (holes and descriptors) from list of tokens.
 *
 * @since 0.1.5
 */
public class ParametersListParser {
    /**
     * The list of tokens.
     */
    private final TokenList tokens;

    /**
     * The label factory.
     */
    private final LabelFactory labels;

    /**
     * Constructor.
     * @param tokens The list of tokens
     * @param labels The label factory
     */
    public ParametersListParser(final TokenList tokens, final LabelFactory labels) {
        this.tokens = tokens;
        this.labels = labels;
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
                result.add(
                    new DescriptorParser(segment, this.labels).parse(DescriptorAttribute.NONE)
                );
            } else if (first instanceof BracketsPair) {
                result.add(this.parseOptional(segment));
            } else if (first instanceof Ampersand) {
                result.add(Extension.INSTANCE);
            } else if (first instanceof EmptySymbol) {
                result.add(Empty.INSTANCE);
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
    private Descriptor parseOptional(final TokenList segment) throws ParserException {
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
        return new DescriptorParser(children, this.labels).parse(attribute);
    }
}
