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
 * Describes a Java interface and generates source code for it.
 * @since 1.0.0
 */
public final class Interface implements ClassOrInterface {
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
    private boolean pub;

    /**
     * A list of interfaces that this interface extends.
     */
    private String[] ext;

    /**
     * Constructor.
     * @param name Name of the interface.
     * @param brief Brief description of the interface
     */
    public Interface(final String name, final String brief) {
        this.name = name;
        this.doc = new JavaDoc(brief);
        this.ext = new String[0];
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the version number. It will be added to JavaDoc.
     * @param value Version number
     */
    public void setVersion(final String value) {
        this.doc.setVersion(value);
    }

    /**
     * Makes the interface public.
     */
    public void makePublic() {
        this.pub = true;
    }

    /**
     * Sets the list of interfaces that this class extends.
     * @param names Interface names
     */
    public void setExtendsList(final String... names) {
        this.ext = names.clone();
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        code.add(indent, this.composeHeader());
        code.add(indent, "}");
    }

    /**
     * Composes the header of the interface.
     * @return Interface header
     */
    private String composeHeader() {
        final StringBuilder header = new StringBuilder(128);
        if (this.pub) {
            header.append("public ");
        }
        header.append("interface ").append(this.name);
        if (this.ext.length > 0) {
            header.append(" extends ");
            boolean flag = false;
            for (final String iface : this.ext) {
                if (flag) {
                    header.append(", ");
                }
                flag = true;
                header.append(iface);
            }
        }
        header.append(" {");
        return header.toString();
    }
}
