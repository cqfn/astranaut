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
package org.cqfn.astranaut.api;

import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.exceptions.ProcessorCouldNotWriteFile;

/**
 * Converts a tree to a string that contains a JSON object.
 *
 * @since 0.2
 */
public class JsonSerializer {
    /**
     * The root node of the tree to be converted.
     */
    private final Tree tree;

    /**
     * Constructor.
     * @param tree The tree to be converted
     */
    public JsonSerializer(final Tree tree) {
        this.tree = tree;
    }

    /**
     * Converts the tree to a string that contains a JSON object.
     * @return The tree represented as a string
     */
    public String serializeToJsonString() {
        final org.cqfn.astranaut.core.utils.JsonSerializer serializer =
            new org.cqfn.astranaut.core.utils.JsonSerializer(this.tree);
        return serializer.serialize();
    }

    /**
     * Converts the tree to a string that contains a JSON object and
     * writes the result to a file.
     * @param filename The file name
     * @throws ProcessorCouldNotWriteFile In case the operation fails
     */
    public void serializeToJsonFile(final String filename)
        throws ProcessorCouldNotWriteFile {
        final org.cqfn.astranaut.core.utils.JsonSerializer serializer =
            new org.cqfn.astranaut.core.utils.JsonSerializer(this.tree);
        if (!serializer.serializeToFile(filename)) {
            throw new ProcessorCouldNotWriteFile(filename);
        }
    }
}
