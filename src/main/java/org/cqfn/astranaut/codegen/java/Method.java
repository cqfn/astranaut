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
 * Describes a method and generates source code for it.
 * @since 1.0.0
 */
public final class Method implements Entity {
    /**
     * Type of return value.
     */
    private final String ret;

    /**
     * Name of the method.
     */
    private final String name;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * Flag indicating that the generated method is public.
     */
    private boolean pub;

    /**
     * Flag indicating that the generated method is protected.
     */
    private boolean prt;

    /**
     * Flag indicating that the generated method is private.
     */
    private boolean pvt;

    /**
     * Flag indicating that the generated method is static.
     */
    private boolean stat;

    /**
     * Flag indicating that the generated method is final.
     */
    private boolean fin;

    /**
     * Constructor.
     * @param ret Type of the method.
     * @param name Name of the method.
     * @param brief Brief description of the method
     */
    public Method(final String ret, final String name, final String brief) {
        this.ret = ret;
        this.name = name;
        this.doc = new JavaDoc(brief);
    }

    /**
     * Makes the method public.
     */
    public void makePublic() {
        this.pub = true;
        this.prt = false;
        this.pvt = false;
    }

    /**
     * Makes the method protected.
     */
    public void makeProtected() {
        this.pub = false;
        this.prt = true;
        this.pvt = false;
    }

    /**
     * Makes the method private.
     */
    public void makePrivate() {
        this.pub = false;
        this.prt = false;
        this.pvt = true;
    }

    /**
     * Makes the method static.
     */
    public void makeStatic() {
        this.stat = true;
    }

    /**
     * Makes the method final.
     */
    public void makeFinal() {
        this.fin = true;
    }

    /**
     * Returns the priority of the method.
     *  Fields with higher priority are placed at the beginning of classes.
     * @return Priority of the method
     */
    public int getPriority() {
        final int priority;
        if (!this.stat && this.pub) {
            priority = 4;
        } else if (this.pub) {
            priority = 3;
        } else if (this.stat) {
            priority = 1;
        } else {
            priority = 2;
        }
        return priority;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        final StringBuilder header = new StringBuilder(128);
        if (this.pub) {
            header.append("public ");
        } else if (this.prt) {
            header.append("protected ");
        } else if (this.pvt) {
            header.append("private ");
        }
        if (this.stat) {
            header.append("static ");
        }
        if (this.fin) {
            header.append("final ");
        }
        header.append(this.ret).append(' ').append(this.name).append("() {");
        code.add(indent, header.toString());
        code.add(indent, "}");
    }
}
