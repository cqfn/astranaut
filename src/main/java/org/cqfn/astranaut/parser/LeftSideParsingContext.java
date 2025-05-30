/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.Stack;

/**
 * Data that is required during parsing of the left parts of transformation rules.
 * @since 1.0.0
 */
final class LeftSideParsingContext {
    /**
     * Scanner that issues tokens.
     */
    private final Scanner scanner;

    /**
     * Tokens that have been received from the scanner but not used.
     */
    private final Stack<Token> tokens;

    /**
     * Pattern nesting level.
     */
    private int nesting;

    /**
     * Counter for tracking placeholders (holes).
     */
    private final HoleCounter holes;

    /**
     * Constructor.
     * @param scanner Scanner that issues tokens
     */
    LeftSideParsingContext(final Scanner scanner) {
        this.scanner = scanner;
        this.tokens = new Stack<>();
        this.nesting = 0;
        this.holes = new HoleCounter();
    }

    /**
     * Returns the next token.
     * @return A token or {@code null} if no more tokens
     * @throws ParsingException If the scanner fails
     */
    Token getToken() throws ParsingException {
        final Token token;
        if (this.tokens.empty()) {
            token = this.scanner.getToken();
        } else {
            token = this.tokens.pop();
        }
        return token;
    }

    /**
     * Returns location of the DSL code.
     * @return Location of the DSL code
     */
    Location getLocation() {
        return this.scanner.getLocation();
    }

    /**
     * Saves an unused token so that another parser can use it.
     * @param token A token
     */
    void pushToken(final Token token) {
        if (token != null) {
            this.tokens.push(token);
        }
    }

    /**
     * Returns the current nesting level of patterns.
     * @return Nesting level
     */
    int getNestingLevel() {
        return this.nesting;
    }

    /**
     * Increases the nesting level by 1.
     */
    void incrementNestingLevel() {
        this.nesting = this.nesting + 1;
    }

    /**
     * Decreases the nesting level by 1.
     */
    void decrementNestingLevel() {
        if (this.nesting == 0) {
            throw new UnsupportedOperationException();
        }
        this.nesting = this.nesting - 1;
    }

    /**
     * Returns the counter for tracking placeholders (holes).
     * @return Hole counter
     */
    HoleCounter getHoleCounter() {
        return this.holes;
    }
}
