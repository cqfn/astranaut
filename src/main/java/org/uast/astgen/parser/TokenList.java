/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.Iterator;
import org.uast.astgen.scanner.Token;

/**
 * A list contains tokens.
 *
 * @since 1.0
 */
public abstract class TokenList implements Iterable<Token> {
    /**
     * Returns the size of the list.
     * @return The size
     */
    public abstract int size();

    /**
     * Returns token by its index.
     * @param index Index
     * @return A token
     * @throws IndexOutOfBoundsException If index is negative or greater than size - 1
     */
    public abstract Token get(int index) throws IndexOutOfBoundsException;

    @Override
    public final Iterator<Token> iterator() {
        return new TokenListIterator(this);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < this.size(); index = index + 1) {
            builder.append(this.get(index).toString());
        }
        return builder.toString();
    }
}
