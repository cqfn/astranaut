/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.Iterator;
import java.util.Stack;
import org.uast.astgen.scanner.Token;

/**
 * Stack containing tokens, allowing to store an unused token.
 *
 * @since 1.0
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
