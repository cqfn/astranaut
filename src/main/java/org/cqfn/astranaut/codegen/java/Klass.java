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
import java.util.stream.Collectors;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Describes a Java class and generates source code for it.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
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
     * Suppresses compiler or codechecker warnings.
     */
    private final Suppress suppress;

    /**
     * Flag indicating that the generated class is public.
     */
    private boolean pub;

    /**
     * Flag indicating that the generated class is protected.
     */
    private boolean prt;

    /**
     * Flag indicating that the generated class is private.
     */
    private boolean pvt;

    /**
     * Flag indicating that the generated class is static.
     */
    private boolean stat;

    /**
     * Flag indicating that the generated class is final.
     */
    private boolean fin;

    /**
     * The name of the superclass from which this class inherits.
     */
    private String ext;

    /**
     * A list of interfaces that this class implements.
     */
    private String[] impl;

    /**
     * List of fields.
     */
    private final List<Field> fields;

    /**
     * List of constructors.
     */
    private final List<Constructor> constructors;

    /**
     * List of methods.
     */
    private final List<Method> methods;

    /**
     * List of nested classes and interfaces.
     */
    private final List<ClassOrInterface> nested;

    /**
     * Constructor.
     * @param name Name of the class.
     * @param brief Brief description of the class
     */
    public Klass(final String name, final String brief) {
        this.name = name;
        this.doc = new JavaDoc(brief);
        this.suppress = new Suppress();
        this.ext = "";
        this.impl = new String[0];
        this.fields = new ArrayList<>(0);
        this.constructors = new ArrayList<>(0);
        this.methods = new ArrayList<>(0);
        this.nested = new ArrayList<>(0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Adds a warning that needs to be suppressed.
     * @param warning Warning
     */
    public void suppressWarning(final String warning) {
        this.suppress.addWarning(warning);
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
        this.pub = true;
        this.prt = false;
        this.pvt = false;
    }

    /**
     * Makes the class protected.
     */
    public void makeProtected() {
        this.pub = false;
        this.prt = true;
        this.pvt = false;
    }

    /**
     * Makes the class private.
     */
    public void makePrivate() {
        this.pub = false;
        this.prt = false;
        this.pvt = true;
    }

    /**
     * Makes the class static.
     */
    public void makeStatic() {
        this.stat = true;
    }

    /**
     * Makes the class final.
     */
    public void makeFinal() {
        this.fin = true;
    }

    /**
     * Sets the name of the superclass from which this class inherits.
     * @param classname Class name
     */
    public void setSuperclass(final String classname) {
        this.ext = classname;
    }

    /**
     * Sets the list of interfaces that this class implements.
     * @param names Interface names
     */
    public void setImplementsList(final String... names) {
        this.impl = names.clone();
    }

    /**
     * Adds a field.
     * @param field Field
     */
    public void addField(final Field field) {
        this.fields.add(field);
    }

    /**
     * Adds a method.
     * @param method Method
     */
    public void addMethod(final Method method) {
        this.methods.add(method);
    }

    /**
     * Adds a nested class or interface.
     * @param coi Class or interface
     */
    public void addNested(final ClassOrInterface coi) {
        this.nested.add(coi);
    }

    /**
     * Creates a class constructor.
     * @return An entity representing the constructor for this class
     */
    public Constructor createConstructor() {
        final Constructor entity = new Constructor(this.name);
        this.constructors.add(entity);
        return entity;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        this.suppress.build(indent, code);
        code.add(indent, this.composeHeader());
        boolean flag = false;
        final List<Field> flist = this.fields.stream()
            .sorted((left, right) -> Integer.compare(right.getPriority(), left.getPriority()))
            .collect(Collectors.toList());
        for (final Field field : flist) {
            code.addEmpty(flag);
            flag = true;
            field.build(indent + 1, code);
        }
        for (final Constructor ctor : this.constructors) {
            code.addEmpty(flag);
            flag = true;
            ctor.build(indent + 1, code);
        }
        final List<Method> mlist = this.methods.stream()
            .sorted((left, right) -> Integer.compare(right.getPriority(), left.getPriority()))
            .collect(Collectors.toList());
        for (final Method method : mlist) {
            code.addEmpty(flag);
            flag = true;
            method.build(indent + 1, code);
        }
        for (final ClassOrInterface coi : this.nested) {
            code.addEmpty(flag);
            flag = true;
            coi.build(indent + 1, code);
        }
        code.add(indent, "}");
    }

    /**
     * Composes the header of the class.
     * @return Class header
     */
    private String composeHeader() {
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
        header.append("class ").append(this.name);
        if (!this.ext.isEmpty()) {
            header.append(" extends ").append(this.ext);
        }
        if (this.impl.length > 0) {
            header.append(" implements ");
            boolean flag = false;
            for (final String iface : this.impl) {
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
