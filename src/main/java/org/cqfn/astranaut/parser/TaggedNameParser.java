/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import org.cqfn.astranaut.exceptions.ExpectedIdentifierAfterAt;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.DescriptorFactory;
import org.cqfn.astranaut.scanner.AtSign;
import org.cqfn.astranaut.scanner.Identifier;
import org.cqfn.astranaut.scanner.Token;

/**
 * Parser for tagged names.
 *
 * @since 0.1.5
 */
public class TaggedNameParser {
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
    public TaggedNameParser(final TokenStack stack, final DescriptorFactory factory) {
        this.stack = stack;
        this.factory = factory;
        this.flag = false;
    }

    /**
     * Parses tagged name.
     * @throws ParserException If unused tokens cannot be converted to a tagged name
     */
    public void parse() throws ParserException {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        if (this.checkCondition()) {
            if (!this.stack.hasTokens()) {
                throw ExpectedIdentifierAfterAt.INSTANCE;
            }
            final Token token = this.stack.pop();
            if (!(token instanceof Identifier)) {
                throw ExpectedIdentifierAfterAt.INSTANCE;
            }
            this.factory.replaceType(((Identifier) token).getValue());
        }
    }

    /**
     * Checks whether sequence has tagged name.
     * @return Checking result
     */
    private boolean checkCondition() {
        boolean result = false;
        if (this.stack.hasTokens()) {
            final Token token = this.stack.pop();
            if (token instanceof AtSign) {
                result = true;
            } else {
                this.stack.push(token);
            }
        }
        return result;
    }
}
