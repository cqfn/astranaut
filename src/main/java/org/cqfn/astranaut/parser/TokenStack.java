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

import java.util.Iterator;
import java.util.Stack;
import org.cqfn.astranaut.scanner.Token;

/**
 * Stack containing tokens, allowing to store an unused token.
 *
 * @since 0.1.5
 */
public class TokenStack {
    /**
     * Source iterator.
     */
    private final Iterator<Token> iterator;

    /**
     * Stack to save unused tokens.
     */
    private final Stack<Token> stack;

    /**
     * Constructor.
     * @param iterator Source iterator
     */
    public TokenStack(final Iterator<Token> iterator) {
        this.iterator = iterator;
        this.stack = new Stack<>();
    }

    /**
     * Adds the token to the top of the stack.
     * @param token Token
     */
    public void push(final Token token) {
        this.stack.push(token);
    }

    /**
     * Removes a token from the top of the stack.
     * @return Removed token
     */
    public Token pop() {
        final Token result;
        if (this.stack.isEmpty()) {
            result = this.iterator.next();
        } else {
            result = this.stack.pop();
        }
        return result;
    }

    /**
     * Checks whether the stack has tokens.
     * @return The checking result
     */
    public boolean hasTokens() {
        return !this.stack.isEmpty() || this.iterator.hasNext();
    }
}
