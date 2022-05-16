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

import java.util.Map;

/**
 * The node factory.
 *
 * @since 1.0
 */
public class Factory {
    /**
     * The set of types arranged by name.
     */
    private final Map<String, Type> types;

    /**
     * Constructor.
     * @param types The set of types arranged by name
     */
    public Factory(final Map<String, Type> types) {
        this.types = types;
    }

    /**
     * Creates node builder by type name.
     * @param name The type name
     * @return A node builder
     */
    public final Builder createBuilder(final String name) {
        final Builder result;
        if (this.types.containsKey(name)) {
            final Type type = this.types.get(name);
            result = type.createBuilder();
        } else {
            final DraftNode.Constructor draft = new DraftNode.Constructor();
            draft.setName(name);
            result = draft;
        }
        return result;
    }
}
