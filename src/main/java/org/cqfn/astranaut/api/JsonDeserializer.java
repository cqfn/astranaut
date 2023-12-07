/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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
package org.cqfn.astranaut.api;

import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.interpreter.DefaultFactory;

/**
 * Converts a string that contains a JSON object to a tree.
 *
 * @since 0.2
 */
public class JsonDeserializer {
    /**
     * The string that contains a JSON object.
     */
    private final String source;

    /**
     * Constructor.
     * @param source The source string
     */
    public JsonDeserializer(final String source) {
        this.source = source;
    }

    /**
     * Converts the source string that contains a JSON object to a tree.
     * @return Root node of the created tree
     */
    public Node deserialize() {
        final org.cqfn.astranaut.core.utils.JsonDeserializer deserializer =
            new org.cqfn.astranaut.core.utils.JsonDeserializer(
                this.source, language -> DefaultFactory.INSTANCE
            );
        return deserializer.convert();
    }
}
