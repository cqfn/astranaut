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
package org.cqfn.astranaut.codegen.java;

import java.util.Set;
import java.util.TreeSet;

/**
 * Java imports block.
 *
 * @since 0.1.5
 */
public final class Imports {
    /**
     * The set of imports.
     */
    private final Set<String> set;

    /**
     * Constructor.
     */
    public Imports() {
        this.set = new TreeSet<>();
    }

    /**
     * Adds item to the set.
     * @param item The item
     */
    public void addItem(final String item) {
        this.set.add(item);
    }

    /**
     * Checks whether the set has items.
     * @return Checking result
     */
    public boolean hasItems() {
        return !this.set.isEmpty();
    }

    /**
     * Generates source code from the set.
     * @return Source code
     */
    public String generate() {
        final StringBuilder builder = new StringBuilder();
        for (final String item : this.set) {
            builder.append("import ").append(item).append(";\n");
        }
        builder.append('\n');
        return builder.toString();
    }
}
