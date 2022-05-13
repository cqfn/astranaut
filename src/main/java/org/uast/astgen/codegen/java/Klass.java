/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.uast.astgen.codegen.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.uast.astgen.utils.StringUtils;

/**
 * Java class.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Klass implements Type {
    /**
     * The brief description.
     */
    private final String brief;

    /**
     * The version.
     */
    private String version;

    /**
     * The flag indicates that the class is public.
     */
    private boolean fpublic;

    /**
     * The flag indicates that the class is private.
     */
    private boolean fprivate;

    /**
     * The flag indicates that the class is static.
     */
    private boolean fstatic;

    /**
     * The flag indicates that the class is final.
     */
    private boolean ffinal;

    /**
     * The class name.
     */
    private final String name;

    /**
     * The name of the parent class.
     */
    private String parent;

    /**
     * List of implemented interfaces.
     */
    private List<String> interfaces;

    /**
     * The list of fields.
     */
    private final List<Field> fields;

    /**
     * The list of constructors.
     */
    private final List<Constructor> constructors;

    /**
     * The list of methods.
     */
    private final List<Method> methods;

    /**
     * The list of inner classes.
     */
    private final List<Klass> classes;

    /**
     * Constructor.
     * @param brief The brief description
     * @param name The class name
     */
    public Klass(final String brief, final String name) {
        this.brief = brief;
        this.version = "1.0";
        this.fpublic = true;
        this.name = name;
        this.parent = "";
        this.interfaces = Collections.emptyList();
        this.fields = new ArrayList<>(0);
        this.constructors = new ArrayList<>(0);
        this.methods = new ArrayList<>(0);
        this.classes = new ArrayList<>(0);
    }

    /**
     * Returns the class name.
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the parent class name.
     * @param str The parent class name
     */
    public void setParentClass(final String str) {
        this.parent = Objects.requireNonNull(str);
    }

    /**
     * Set the list of interface names, that the class implements.
     * @param list The list of interface names
     */
    public void setInterfaces(final String... list) {
        this.interfaces = Arrays.asList(list);
    }

    /**
     * Resets all flags.
     */
    public void resetFlags() {
        this.fpublic = false;
        this.fprivate = false;
        this.fstatic = false;
        this.ffinal = false;
    }

    /**
     * Makes this class public.
     */
    public void makePublic() {
        this.fpublic = true;
        this.fprivate = false;
    }

    /**
     * Makes this class private.
     */
    public void makePrivate() {
        this.fpublic = false;
        this.fprivate = true;
    }

    /**
     * Makes this class static.
     */
    public void makeStatic() {
        this.fstatic = true;
    }

    /**
     * Makes this field final.
     */
    public void makeFinal() {
        this.ffinal = true;
    }

    /**
     * Adds the field to the class.
     * @param field The field
     */
    public void addField(final Field field) {
        this.fields.add(field);
    }

    /**
     * Adds the constructor to the class.
     * @param constructor The constructor
     */
    public void addConstructor(final Constructor constructor) {
        this.constructors.add(constructor);
    }

    /**
     * Adds the method to the class.
     * @param method The method
     */
    public void addMethod(final Method method) {
        this.methods.add(method);
    }

    /**
     * Adds the inner class to the class.
     * @param klass The inner class
     */
    public void addClass(final Klass klass) {
        this.classes.add(klass);
    }

    @Override
    public String getBrief() {
        return this.brief;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(final String str) {
        this.version = Objects.requireNonNull(str);
    }

    /**
     * Makes this class a singleton.
     */
    public void makeSingleton() {
        String type = this.name;
        if (this.parent.isEmpty() && this.interfaces.size() == 1) {
            type = this.interfaces.get(0);
        }
        final Field field = new Field("The instance", type, "INSTANCE");
        field.makePublic();
        field.makeStaticFinal();
        field.setInitExpr(String.format("new %s()", this.name));
        this.addField(field);
        final Constructor ctor = new Constructor(this.name);
        ctor.makePrivate();
        this.addConstructor(ctor);
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(128);
        this.generateHeader(builder, indent);
        builder.append(tabulation);
        if (this.fprivate) {
            builder.append("private ");
        } else if (this.fpublic) {
            builder.append("public ");
        }
        if (this.fstatic) {
            builder.append("static ");
        }
        if (this.ffinal) {
            builder.append("final ");
        }
        builder.append("class ").append(this.name);
        this.generateParents(builder);
        builder.append(" {\n");
        boolean flag = this.generateFields(builder, false, indent + 1);
        flag = this.generateConstructors(builder, flag, indent + 1);
        flag = this.generateMethods(builder, flag, indent + 1);
        this.generateInnerClasses(builder, flag, indent + 1);
        builder.append(tabulation).append("}\n");
        return builder.toString();
    }

    /**
     * Generates parents list (what the class extends and implements).
     * @param builder String builder where to generate
     */
    private void generateParents(final StringBuilder builder) {
        if (!this.parent.isEmpty()) {
            builder.append(" extends ").append(this.parent);
        }
        if (!this.interfaces.isEmpty()) {
            builder.append(" implements ");
            boolean comma = false;
            for (final String iface : this.interfaces) {
                if (comma) {
                    builder.append(", ");
                }
                comma = true;
                builder.append(iface);
            }
        }
    }

    /**
     * Generated source code for fields.
     * @param builder String builder where to generate
     * @param separator Flag that indicated that need to add empty line after previous entity
     * @param indent Indentation
     * @return New separator flag
     */
    private boolean generateFields(
        final StringBuilder builder,
        final boolean separator,
        final int indent
    ) {
        boolean flag = separator;
        for (final Field field : this.fields) {
            if (!field.isStatic()) {
                continue;
            }
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(field.generate(indent));
        }
        for (final Field field : this.fields) {
            if (field.isStatic()) {
                continue;
            }
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(field.generate(indent));
        }
        return flag;
    }

    /**
     * Generated source code for constructors.
     * @param builder String builder where to generate
     * @param separator Flag that indicated that need to add empty line after previous entity
     * @param indent Indentation
     * @return New separator flag
     */
    private boolean generateConstructors(
        final StringBuilder builder,
        final boolean separator,
        final int indent
    ) {
        boolean flag = separator;
        for (final Constructor constructor : this.constructors) {
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(constructor.generate(indent));
        }
        return flag;
    }

    /**
     * Generated source code for methods.
     * @param builder String builder where to generate
     * @param separator Flag that indicated that need to add empty line after previous entity
     * @param indent Indentation
     * @return New separator flag
     */
    private boolean generateMethods(
        final StringBuilder builder,
        final boolean separator,
        final int indent
    ) {
        boolean flag = separator;
        for (final Method method : this.methods) {
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(method.generate(indent));
        }
        return flag;
    }

    /**
     * Generated source code for inner classes.
     * @param builder String builder where to generate
     * @param separator Flag that indicated that need to add empty line after previous entity
     * @param indent Indentation
     * @return New separator flag
     */
    private boolean generateInnerClasses(
        final StringBuilder builder,
        final boolean separator,
        final int indent
    ) {
        boolean flag = separator;
        for (final Klass klass : this.classes) {
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(klass.generate(indent));
        }
        return flag;
    }
}
