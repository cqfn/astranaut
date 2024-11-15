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
 * Describes a constructor (constructor without return value) and generates source code for it.
 * @since 1.0.0
 */
public final class Constructor extends BaseMethod {
    /**
     * Name of the constructor.
     */
    private final String name;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * List of constructor arguments (where key is type, value is name).
     */
    private final List<Pair<String, String>> args;

    /**
     * Body of the constructor.
     */
    private String body;

    /**
     * Constructor.
     * @param name Name of the constructor.
     */
    public Constructor(final String name) {
        this.name = name;
        this.doc = new JavaDoc("Constructor");
        this.args = new ArrayList<>(0);
        this.body = "";
    }

    /**
     * Adds an argument to the constructor.
     * @param type Type of the argument
     * @param identifier Name of the argument
     */
    public void addArgument(final String type, final String identifier) {
        this.args.add(new Pair<>(type, identifier));
    }

    /**
     * Sets the body of the constructor.
     * @param text Method body source code
     */
    public void setBody(final String text) {
        this.body = text;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        code.add(indent, this.composeHeader());
        this.buildBody(indent + 1, code);
        code.add(indent, "}");
    }

    @Override
    public String getBody() {
        return this.body;
    }

    /**
     * Composes the header (signature) of the constructor.
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
        header.append(this.name).append('(');
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
