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
package org.cqfn.astgen.scanner;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
