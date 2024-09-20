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
package org.cqfn.astranaut.scanner;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The factory for bracket tokens.
 *
 * @since 0.1.5
 */
final class BracketFactory {
    /**
     * The instance.
     */
    public static final BracketFactory INSTANCE = new BracketFactory();

    /**
     * A set of objects mapped to the parentheses symbol.
     */
    private final Map<Character, Bracket> objects;

    /**
     * Constructor.
     */
    private BracketFactory() {
        this.objects = Collections.unmodifiableMap(BracketFactory.createMap());
    }

    /**
     * Returns bracket instance by its symbol.
     * @param symbol A symbol
     * @return A bracket instance
     */
    Bracket getObject(final char symbol) {
        if (this.objects.containsKey(symbol)) {
            return this.objects.get(symbol);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Creates a set of objects mapped to the parentheses symbol.
     * @return A map
     */
    private static Map<Character, Bracket> createMap() {
        final Map<Character, Bracket> map = new TreeMap<>();
        map.put('(', OpeningRoundBracket.INSTANCE);
        map.put('{', OpeningCurlyBracket.INSTANCE);
        map.put('[', OpeningSquareBracket.INSTANCE);
        map.put('<', OpeningAngleBracket.INSTANCE);
        map.put(')', ClosingRoundBracket.INSTANCE);
        map.put('}', ClosingCurlyBracket.INSTANCE);
        map.put(']', ClosingSquareBracket.INSTANCE);
        map.put('>', ClosingAngleBracket.INSTANCE);
        return map;
    }
}
