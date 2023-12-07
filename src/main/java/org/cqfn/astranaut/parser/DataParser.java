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

import org.cqfn.astranaut.exceptions.EmptyDataLiteral;
import org.cqfn.astranaut.exceptions.ExpectedData;
import org.cqfn.astranaut.exceptions.ExpectedOnlyOneEntity;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.DescriptorFactory;
import org.cqfn.astranaut.scanner.AngleBracketsPair;
import org.cqfn.astranaut.scanner.HoleMarker;
import org.cqfn.astranaut.scanner.StringToken;
import org.cqfn.astranaut.scanner.Token;
import org.cqfn.astranaut.scanner.TokenList;

/**
 * Parses data inside descriptor.
 *
 * @since 0.1.5
 */
public class DataParser {
    /**
     * Stack containing unused tokens.
     */
    private final TokenStack stack;

    /**
     * Descriptor factory.
     */
    private final DescriptorFactory factory;

    /**
     * Flag that indicates that the parser has worked.
     */
    private boolean flag;

    /**
     * Constructor.
     * @param stack Stack containing unused tokens
     * @param factory Descriptor factory
     */
    public DataParser(final TokenStack stack, final DescriptorFactory factory) {
        this.stack = stack;
        this.factory = factory;
        this.flag = false;
    }

    /**
     * Parses data inside descriptor.
     * @throws ParserException If unused tokens cannot be parsed as a data
     */
    public void parse() throws ParserException {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        do {
            if (!this.stack.hasTokens()) {
                break;
            }
            final Token token = this.stack.pop();
            if (!(token instanceof AngleBracketsPair)) {
                this.stack.push(token);
                break;
            }
            final TokenList children = ((AngleBracketsPair) token).getTokens();
            this.extractData(children);
        } while (false);
    }

    /**
     * Extracts data from the list of tokens.
     * @param tokens The list of tokens
     * @throws ParserException If unused tokens cannot be converted to a data
     */
    private void extractData(final TokenList tokens)
        throws ParserException {
        final int count = tokens.size();
        if (count == 0) {
            throw new EmptyDataLiteral(this.factory.createDescriptor().getFullName());
        }
        if (count > 1) {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.factory.createDescriptor().getFullName())
                .append('<')
                .append(tokens.toString())
                .append('>');
            throw new ExpectedOnlyOneEntity(builder.toString());
        }
        final Token child = tokens.get(0);
        if (child instanceof HoleMarker) {
            this.factory.setData(((HoleMarker) child).createHole());
        } else if (child instanceof StringToken) {
            this.factory.setData(((StringToken) child).createStringData());
        } else {
            throw new ExpectedData(this.factory.createDescriptor().getFullName());
        }
    }
}
