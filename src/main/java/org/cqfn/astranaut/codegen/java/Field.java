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
 * Describes a field and generates source code for it.
 * @since 1.0.0
 */
public final class Field implements Entity {
    /**
     * Type of the field.
     */
    private final String type;

    /**
     * Name of the field.
     */
    private final String name;

    /**
     * Initial value of the field.
     */
    private String initial;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * Flag indicating that the generated field is public.
     */
    private boolean pub;

    /**
     * Flag indicating that the generated field is protected.
     */
    private boolean prt;

    /**
     * Flag indicating that the generated field is private.
     */
    private boolean pvt;

    /**
     * Flag indicating that the generated field is static.
     */
    private boolean stat;

    /**
     * Flag indicating that the generated field is final.
     */
    private boolean fin;

    /**
     * Constructor.
     * @param type Type of the field.
     * @param name Name of the field.
     * @param brief Brief description of the field
     */
    public Field(final String type, final String name, final String brief) {
        this.type = type;
        this.name = name;
        this.doc = new JavaDoc(brief);
        this.initial = "";
    }

    /**
     * Makes the field public.
     */
    public void makePublic() {
        this.pub = true;
        this.prt = false;
        this.pvt = false;
    }

    /**
     * Makes the field protected.
     */
    public void makeProtected() {
        this.pub = false;
        this.prt = true;
        this.pvt = false;
    }

    /**
     * Makes the field private.
     */
    public void makePrivate() {
        this.pub = false;
        this.prt = false;
        this.pvt = true;
    }

    /**
     * Makes the field static.
     */
    public void makeStatic() {
        this.stat = true;
    }

    /**
     * Makes the field final.
     * @param value Initial value of the field
     */
    public void makeFinal(final String value) {
        this.fin = true;
        this.initial = value;
    }

    /**
     * Sets the initial value of the field.
     * @param value Initial value of the field
     */
    public void setInitial(final String value) {
        this.initial = value;
    }

    /**
     * Returns the priority of the field.
     *  Fields with higher priority are placed at the beginning of classes.
     * @return Priority of the field
     */
    public int getPriority() {
        final int priority;
        if (this.stat && this.pub) {
            priority = 4;
        } else if (this.stat) {
            priority = 3;
        } else if (this.pub) {
            priority = 2;
        } else {
            priority = 1;
        }
        return priority;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        final StringBuilder builder = new StringBuilder(128);
        if (this.pub) {
            builder.append("public ");
        } else if (this.prt) {
            builder.append("protected ");
        } else if (this.pvt) {
            builder.append("private ");
        }
        if (this.stat) {
            builder.append("static ");
        }
        if (this.fin) {
            builder.append("final ");
        }
        builder.append(this.type).append(' ').append(this.name);
        if (!this.initial.isEmpty()) {
            builder.append(" = ").append(this.initial);
        }
        builder.append(';');
        code.add(indent, builder.toString());
    }
}
