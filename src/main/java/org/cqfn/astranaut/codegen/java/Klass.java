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

import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Describes a Java class and allows to generate source code for it.
 * @since 1.0.0
 */
public final class Klass implements ClassOrInterface {
    /**
     * Name of the class.
     */
    private final String name;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * Flag indicating that the generated class is public.
     */
    private boolean fpublic;

    /**
     * Constructor.
     * @param name Name of the class.
     * @param brief Brief description of the class
     */
    public Klass(final String name, final String brief) {
        this.name = name;
        this.doc = new JavaDoc(brief);
    }

    /**
     * Sets the version number. It will be added to JavaDoc.
     * @param value Version number
     */
    public void setVersion(final String value) {
        this.doc.setVersion(value);
    }

    /**
     * Makes the class public.
     */
    public void makePublic() {
        this.fpublic = true;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        final StringBuilder header = new StringBuilder();
        if (this.fpublic) {
            header.append("public ");
        }
        header.append("class ").append(this.name).append(" {");
        code.add(indent, header.toString());
        code.add(indent, "}");
    }
}
