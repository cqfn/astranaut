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

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Describes a method and generates source code for it.
 * @since 1.0.0
 */
public final class Method extends BaseMethod {
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
     * Suppresses compiler or codechecker warnings.
     */
    private final Suppress suppress;

    /**
     * Flag indicating that the generated method is overridden.
     */
    private final boolean over;

    /**
     * Flag indicating that the generated method is static.
     */
    private boolean stat;

    /**
     * Flag indicating that the generated method is final.
     */
    private boolean fin;

    /**
     * List of method arguments (where key is type, value is name).
     */
    private final List<Pair<String, String>> args;

    /**
     * Body of the method.
     */
    private String body;

    /**
     * Constructor of overridden method.
     * @param ret Type of the method.
     * @param name Name of the method.
     */
    public Method(final String ret, final String name) {
        this(ret, name, "");
    }

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
        this.suppress = new Suppress();
        this.over = brief.isEmpty();
        this.args = new ArrayList<>(0);
        this.body = "";
    }

    /**
     * Adds a warning that needs to be suppressed.
     * @param warning Warning
     */
    public void suppressWarning(final String warning) {
        this.suppress.addWarning(warning);
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
     * Adds an argument to the method.
     * @param type Type of the argument
     * @param identifier Name of the argument
     */
    public void addArgument(final String type, final String identifier) {
        this.args.add(new Pair<>(type, identifier));
    }

    /**
     * Adds an argument to the method.
     * @param type Type of the argument
     * @param identifier Name of the argument
     * @param brief Brief description of the argument
     */
    public void addArgument(final String type, final String identifier, final String brief) {
        this.addArgument(type, identifier);
        this.doc.addParameter(identifier, brief);
    }

    /**
     * Sets the body of the method.
     * @param text Method body source code
     */
    public void setBody(final String text) {
        this.body = text;
    }

    /**
     * Adds a description to be printed after the '@return' tag.
     * @param description Description
     */
    public void setReturnsDescription(final String description) {
        this.doc.setReturnsDescription(description);
    }

    /**
     * Returns the priority of the method.
     *  Fields with higher priority are placed at the beginning of classes.
     * @return Priority of the method
     */
    public int getPriority() {
        final int priority;
        if (!this.stat && this.isPublic()) {
            priority = 4;
        } else if (this.isPublic()) {
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
        if (this.doc.hasNonEmptyBrief()) {
            this.doc.build(indent, code);
        }
        this.suppress.build(indent, code);
        if (this.over) {
            code.add(indent, "@Override");
        }
        code.add(indent, this.composeHeader());
        this.buildBody(indent + 1, code);
        code.add(indent, "}");
    }

    @Override
    public String getBody() {
        return this.body;
    }

    /**
     * Composes the header (signature) of the method.
     * @return Method header
     */
    private String composeHeader() {
        final StringBuilder header = new StringBuilder(128);
        if (this.isPublic()) {
            header.append("public ");
        } else if (this.isProtected()) {
            header.append("protected ");
        } else if (this.isPrivate()) {
            header.append("private ");
        }
        if (this.stat) {
            header.append("static ");
        }
        if (this.fin) {
            header.append("final ");
        }
        header.append(this.ret).append(' ').append(this.name).append('(');
        boolean flag = false;
        for (final Pair<String, String> arg : this.args) {
            if (flag) {
                header.append(", ");
            }
            flag = true;
            header.append("final ").append(arg.getKey()).append(' ').append(arg.getValue());
        }
        header.append(") {");
        return header.toString();
    }
}
