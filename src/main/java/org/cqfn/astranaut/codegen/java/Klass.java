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

/**
 * Describes a Java class and allows to generate source code for it.
 * @since 1.0.0
 */
public class Klass {
    /**
     * Name of the class.
     */
    private final String name;

    /**
     * Constructor.
     * @param name Name of the class.
     */
    public Klass(String name) {
        this.name = name;
    }

    /**
     * Builds the source code for this class.
     * @param indent Initial indentation
     * @param code Source code builder
     */
    public void build(final int indent, final SourceCodeBuilder code) {
        final StringBuilder header = new StringBuilder();
        header.append("class ").append(this.name).append(" {");
        code.add(indent, header.toString());
        code.add(indent, "}");
    }
}
