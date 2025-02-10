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
 * Describes a method signature that is used in interfaces and generates source code for it.
 * @since 1.0.0
 */
public final class MethodSignature implements Entity {
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
     * Flag indicating that the generated method is overridden.
     */
    private final boolean over;

    /**
     * List of method arguments (where key is type, value is name).
     */
    private final List<Pair<String, String>> args;

    /**
     * Constructor of overridden method.
     * @param ret Type of the method.
     * @param name Name of the method.
     */
    public MethodSignature(final String ret, final String name) {
        this(ret, name, "");
    }

    /**
     * Constructor.
     * @param ret Type of the method.
     * @param name Name of the method.
     * @param brief Brief description of the method
     */
    public MethodSignature(final String ret, final String name, final String brief) {
        this.ret = ret;
        this.name = name;
        this.doc = new JavaDoc(brief);
        this.over = brief.isEmpty();
        this.args = new ArrayList<>(0);
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
     * Adds a description to be printed after the '@return' tag.
     * @param description Description
     */
    public void setReturnsDescription(final String description) {
        this.doc.setReturnsDescription(description);
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        if (this.doc.hasNonEmptyBrief()) {
            this.doc.build(indent, code);
        }
        if (this.over) {
            code.add(indent, "@Override");
        }
        code.add(indent, this.composeHeader());
    }

    /**
     * Composes the header (signature) of the method.
     * @return Method header
     */
    private String composeHeader() {
        final StringBuilder header = new StringBuilder(128);
        header.append(this.ret).append(' ').append(this.name).append('(');
        boolean flag = false;
        for (final Pair<String, String> arg : this.args) {
            if (flag) {
                header.append(", ");
            }
            flag = true;
            header.append(arg.getKey()).append(' ').append(arg.getValue());
        }
        header.append(");");
        return header.toString();
    }
}
