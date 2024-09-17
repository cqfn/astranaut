/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constructs classes, fields and methods for rules.
 *
 * @since 0.1.5
 */
abstract class BaseConstructor {
    /**
     * The 'Fragment' string.
     */
    private static final String STR_FRAG_TYPE = "Fragment";

    /**
     * The 'fragment' string.
     */
    private static final String STR_FRAG_VAR = "fragment";

    /**
     * The 'The fragment associated with the node' string.
     */
    private static final String STR_FRAG_BRIEF = "The fragment associated with the node";

    /**
     * The 'Type' string.
     */
    private static final String STR_TYPE = "Type";

    /**
     * The 'String' string.
     */
    private static final String STR_STRING = "String";

    /**
     * The {@code List<String>} type.
     */
    private static final String LIST_STRING = "List<String>";

    /**
     * The {@code Map<String, String>} type.
     */
    private static final String MAP_STRING = "Map<String, String>";

    /**
     * The start of unmodifiable list declaration.
     */
    private static final String LIST_BEGIN = "Collections.unmodifiableList(Arrays.asList(";

    /**
     * The end of unmodifiable list declaration.
     */
    private static final String LIST_END = "))";

    /**
     * The list separator.
     */
    private static final String SEPARATOR = ", ";

    /**
     * The environment.
     */
    private final Environment env;

    /**
     * The class to be filled.
     */
    private final Klass klass;

    /**
     * Flag indicating that the class has been full.
     */
    private boolean flag;

    /**
     * Constructor.
     * @param env The environment
     * @param klass The class to be filled
     */
    BaseConstructor(final Environment env, final Klass klass) {
        this.env = env;
        this.klass = klass;
        this.flag = false;
    }

    /**
     * Runs the constructor.
     */
    public void run() {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        this.construct();
    }

    /**
     * Returns the environment.
     * @return The environment
     */
    protected Environment getEnv() {
        return this.env;
    }

    /**
     * Returns the class to be filled.
     * @return The class
     */
    protected Klass getKlass() {
        return this.klass;
    }

    /**
     * Constructs the class that describe node.
     */
    protected abstract void construct();

    /**
     * Creates a field with the 'Fragment' type and getter for it.
     */
    protected void createFragmentWithGetter() {
        final Field field = new Field(
            BaseConstructor.STR_FRAG_BRIEF,
            BaseConstructor.STR_FRAG_TYPE,
            BaseConstructor.STR_FRAG_VAR
        );
        this.klass.addField(field);
        final Method getter = new Method("getFragment");
        getter.makeOverridden();
        getter.setReturnType(BaseConstructor.STR_FRAG_TYPE);
        getter.setCode("return this.fragment;");
        this.klass.addMethod(getter);
    }

    /**
     * Creates a field with the 'Fragment' type and setter for it.
     */
    protected void createFragmentWithSetter() {
        final Field field = new Field(
            BaseConstructor.STR_FRAG_BRIEF,
            BaseConstructor.STR_FRAG_TYPE,
            BaseConstructor.STR_FRAG_VAR
        );
        field.setInitExpr("EmptyFragment.INSTANCE");
        this.klass.addField(field);
        final Method setter = new Method("setFragment");
        setter.makeOverridden();
        setter.addArgument(BaseConstructor.STR_FRAG_TYPE, "obj");
        setter.setCode("this.fragment = obj;");
        this.klass.addMethod(setter);
    }

    /**
     * Creates a class that implements the node type interface, as well as a static field
     * with an object of this class and a method to get this object.
     * @return The empty class constructor to be filled
     */
    protected Klass createTypeClass() {
        final Field field = new Field("The type", BaseConstructor.STR_TYPE, "TYPE");
        field.makePublic();
        field.makeStaticFinal();
        final String name = String.format("%sType", this.klass.getName());
        field.setInitExpr(String.format("new %s()", name));
        this.klass.addField(field);
        final Method getter = new Method("getType");
        getter.makeOverridden();
        getter.setReturnType(BaseConstructor.STR_TYPE);
        getter.setCode(String.format("return %s.TYPE;", this.getType()));
        this.klass.addMethod(getter);
        final Klass subclass = new Klass(
            String.format("Type descriptor of the '%s' node", this.getType()),
            name
        );
        subclass.makePrivate();
        subclass.makeStatic();
        subclass.setInterfaces(BaseConstructor.STR_TYPE);
        this.klass.addClass(subclass);
        return subclass;
    }

    /**
     * Creates a class that implements the node builder interface.
     * @return The empty class constructor to be filled
     */
    protected Klass createBuilderClass() {
        final Klass subclass = new Klass(
            String.format("Class for '%s' node construction", this.getType()),
            "Constructor"
        );
        subclass.makePublic();
        subclass.makeStatic();
        subclass.makeFinal();
        subclass.setInterfaces("Builder");
        this.klass.addClass(subclass);
        return subclass;
    }

    /**
     * Fills in everything related to the hierarchy.
     * @param ssg Static string constructor
     */
    protected void fillHierarchy(final StaticStringGenerator ssg) {
        final List<String> hierarchy = this.getEnv().getHierarchy(this.getType());
        final StringBuilder init = new StringBuilder(128);
        if (hierarchy.size() > 1) {
            init.append(BaseConstructor.LIST_BEGIN);
            boolean separator = false;
            for (final String item : hierarchy) {
                if (separator) {
                    init.append(BaseConstructor.SEPARATOR);
                }
                separator = true;
                init.append(ssg.getFieldName(item));
            }
            init.append(BaseConstructor.LIST_END);
        } else {
            init.append("Collections.singletonList(")
                .append(ssg.getFieldName(hierarchy.get(0)))
                .append(')');
        }
        final Field field = new Field(
            "Hierarchy",
            BaseConstructor.LIST_STRING,
            "HIERARCHY"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(init.toString());
        this.klass.addField(field);
        final Method getter = new Method("getHierarchy");
        getter.setReturnType(BaseConstructor.LIST_STRING);
        getter.setCode(String.format("return %s.HIERARCHY;", this.klass.getName()));
        this.klass.addMethod(getter);
    }

    /**
     * Creates 'PROPERTIES' static field and the 'getProperty()' method.
     */
    protected void fillProperties() {
        String language = this.env.getLanguage();
        final String color;
        if (language.isEmpty()) {
            language = "common";
            color = "green";
        } else {
            color = "red";
        }
        final List<String> init = Arrays.asList(
            "new MapUtils<String, String>()",
            String.format(".put(\"color\", \"%s\")", color),
            String.format(".put(\"language\", \"%s\")", language),
            ".make()"
        );
        final Field field = new Field(
            "Properties",
            BaseConstructor.MAP_STRING,
            "PROPERTIES"
        );
        field.makeStaticFinal();
        field.setInitExpr(init);
        this.klass.addField(field);
        final Method getter = new Method("getProperties");
        getter.setReturnType(BaseConstructor.MAP_STRING);
        getter.setCode(String.format("return %s.PROPERTIES;", this.klass.getName()));
        this.klass.addMethod(getter);
    }

    /**
     * Returns the type of DSL construction.
     * @return The type name
     */
    protected abstract String getType();
}
