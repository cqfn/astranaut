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
package org.cqfn.astranaut.dsl;

import java.util.HashMap;
import java.util.Map;

/**
 * A non-typed hole, identified only by its number.
 *  This class caches commonly used holes (#0, #1, etc.) to improve performance.
 * @since 1.0.0
 */
public final class UntypedHole implements Hole, DataDescriptor, ResultingSubtreeItem {
    /**
     * A cache to store commonly used UntypedHoles (e.g., #0, #1, etc.)
     *  This reduces the number of duplicate objects.
     */
    private static final Map<Integer, UntypedHole> CACHE = new HashMap<>();

    /**
     * The unique number associated with this hole. For example, #0, #1, etc.
     */
    private final int number;

    /**
     * Private constructor to ensure that holes are cached.
     * @param number The unique number for this hole.
     */
    private UntypedHole(final int number) {
        this.number = number;
    }

    /**
     * Factory method to get a cached or newly created hole.
     * @param number The number of the hole to create or retrieve.
     * @return A cached or new UntypedHole instance.
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static UntypedHole getInstance(final int number) {
        return CACHE.computeIfAbsent(number, UntypedHole::new);
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return String.format("#%d", this.number);
    }
}
