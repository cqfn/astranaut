/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The factory for bracket tokens.
 *
 * @since 1.0
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
