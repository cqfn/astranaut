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
package org.cqfn.astgen.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for building lists.
 *
 * @param <T> Item type
 * @since 1.0
 */
public class ListUtils<T> {
    /**
     * List being built.
     */
    private final List<T> result;

    /**
     * Constructor.
     */
    public ListUtils() {
        this.result = new LinkedList<>();
    }

    /**
     * Adds non-null items to the list. Null items are skipped.
     * @param items Items
     * @return Itself
     */
    @SafeVarargs
    public final ListUtils<T> add(final T... items) {
        for (final T item : items) {
            if (item != null) {
                this.result.add(item);
            }
        }
        return this;
    }

    /**
     * Merges another list with the result. Null items are skipped.
     * @param list Another list
     */
    public final void merge(final List<T> list) {
        if (list != null) {
            for (final T item : list) {
                if (item != null) {
                    this.result.add(item);
                }
            }
        }
    }

    /**
     * Builds an unmodifiable list.
     * @return A list
     */
    public List<T> make() {
        return Collections.unmodifiableList(new ArrayList<>(this.result));
    }
}
