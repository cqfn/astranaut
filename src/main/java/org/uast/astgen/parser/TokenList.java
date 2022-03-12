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
public interface TokenList extends Iterable<Token> {
    /**
     * Returns the size of the list.
     * @return The size
     */
    int size();

    /**
     * Returns token by its index.
     * @param index Index
     * @return A token
     * @throws IndexOutOfBoundsException If index is negative or greater than size - 1
     */
    Token get(int index) throws IndexOutOfBoundsException;

    @Override
    default Iterator<Token> iterator() {
        return new TokenListIterator(this);
    }
}
