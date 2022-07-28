/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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

import java.util.List;
import org.cqfn.astranaut.exceptions.CantParseSequence;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.DescriptorFactory;
import org.cqfn.astranaut.rules.Empty;
import org.cqfn.astranaut.rules.HoleDecorator;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.scanner.HoleMarker;
import org.cqfn.astranaut.scanner.Identifier;
import org.cqfn.astranaut.scanner.RoundBracketsPair;
import org.cqfn.astranaut.scanner.Token;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;

/**
 * Parses list of tokens as a descriptor.
 *
 * @since 0.1.5
 */
public class DescriptorParser {
    /**
     * The list of tokens.
     */
    private final TokenList segment;

    /**
     * The label factory.
     */
    private final LabelFactory labels;

    /**
     * Flag that indicates that the parser has worked.
     */
    private boolean flag;

    /**
     * Stack containing unused tokens.
     */
    private final TokenStack stack;

    /**
     * Constructor.
     * @param segment The list of tokens
     * @param labels The label factory
     */
    public DescriptorParser(final TokenList segment, final LabelFactory labels) {
        this.segment = segment;
        this.labels = labels;
        this.flag = false;
        this.stack = new TokenStack(segment.iterator());
    }

    /**
     * Parses list of tokens as a descriptor.
     * @param attribute Descriptor attribute (optional, list)
     * @return A descriptor
     * @throws ParserException If the token list cannot be parsed as a descriptor
     */
    public Descriptor parse(final DescriptorAttribute attribute) throws ParserException {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        assert this.stack.hasTokens();
        final Token first = this.stack.pop();
        Descriptor result = Empty.INSTANCE;
        if (first instanceof Identifier) {
            final String name = ((Identifier) first).getValue();
            final DescriptorFactory factory = new DescriptorFactory(this.labels.getLabel(), name);
            factory.setAttribute(attribute);
            new TaggedNameParser(this.stack, factory).parse();
            this.parseParameters(factory);
            new DataParser(this.stack, factory).parse();
            if (this.stack.hasTokens()) {
                throw new CantParseSequence(this.segment);
            }
            result = factory.createDescriptor();
        } else if (first instanceof HoleMarker) {
            result = new HoleDecorator(((HoleMarker) first).createHole());
        }
        return result;
    }

    /**
     * Parses parameters inside descriptor.
     * @param factory Descriptor factory
     * @throws ParserException If unused tokens cannot be converted to a parameters list
     */
    private void parseParameters(final DescriptorFactory factory)
        throws ParserException {
        do {
            if (!this.stack.hasTokens()) {
                break;
            }
            final Token token = this.stack.pop();
            if (!(token instanceof RoundBracketsPair)) {
                this.stack.push(token);
                break;
            }
            final TokenList children = ((RoundBracketsPair) token).getTokens();
            final ParametersListParser parser = new ParametersListParser(children, this.labels);
            final List<Parameter> parameters = parser.parse();
            factory.setParameters(parameters);
        } while (false);
    }
}
