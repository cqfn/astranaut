/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.uast.astgen.scanner.Token;

/**
 * Wrapper that implements {@link Iterator} interface.
 *
 * @since 1.0
 */
class TokenListIterator implements Iterator<Token> {
    /**
     * The list instance.
     */
    private final TokenList list;

    /**
     * The current index.
     */
    private int index;

    /**
     * Constructor.
     * @param list The list instance.
     */
    TokenListIterator(final TokenList list) {
        this.list = list;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.list.size();
    }

    @Override
    public Token next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final Token token = this.list.get(this.index);
        this.index = this.index + 1;
        return token;
    }
}
