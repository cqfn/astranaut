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
package org.cqfn.astranaut.rules;

/**
 * An interface for vertices in AST.
 *
 * @since 0.1.5
 */
public abstract class Vertex implements Rule, Comparable<Vertex> {
    /**
     * Returns left part of the rule.
     * @return The type name
     */
    public abstract String getType();

    /**
     * Checks if the vertex is an ordinary node, i.e. not abstract and not a list.
     * @return Checking result
     */
    public abstract boolean isOrdinary();

    /**
     * Checks if the vertex is an abstract node.
     * @return Checking result
     */
    public abstract boolean isAbstract();

    /**
     * Checks if the vertex is final, that is non-inheritable.
     *
     * @return Checking result
     */
    public abstract boolean isFinal();
}
