/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

/**
 * Various flags that are set during source code generation by the transformation rule.
 * @since 1.0.0
 */
final class TransformationGeneratorFlags {
    /**
     * Flag indicating that we need to import the 'Collections' class.
     */
    private boolean collections;

    /**
     * Flag indicating that we need to import the 'ListUtils' class.
     */
    private boolean lutils;

    /**
     * Sets the flag indicating that we need to import the 'Collections' class.
     */
    void needCollections() {
        this.collections = true;
    }

    /**
     * Sets the flag indicating that we need to import the 'ListUtils' class.
     */
    void needListUtils() {
        this.lutils = true;
    }

    /**
     * Returns the flag indicating that we need to import the 'Collections' class.
     * @return The flag
     */
    boolean isCollectionsClassNeeded() {
        return this.collections;
    }

    /**
     * Returns the flag indicating that we need to import the 'ListUtils' class.
     * @return The flag
     */
    boolean isListUtilsClassNeeded() {
        return this.lutils;
    }
}
