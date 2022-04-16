/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import org.uast.astgen.rules.Node;

/**
 * Constructs classes, fields and methods for rules that describe nodes.
 *
 * @since 1.0
 */
abstract class NodeConstructor {
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
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * The 'int' string.
     */
    private static final String STR_BOOLEAN = "boolean";

    /**
     * The {@code List<String>} type.
     */
    private static final String LIST_STRING = "List<String>";

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
     * The rule.
     */
    private final Node rule;

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
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeConstructor(final Environment env, final Node rule, final Klass klass) {
        this.env = env;
        this.rule = rule;
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
     * Returns the rule.
     * @return The rule
     */
    protected Node getRule() {
        return this.rule;
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
            NodeConstructor.STR_FRAG_BRIEF,
            NodeConstructor.STR_FRAG_TYPE,
            NodeConstructor.STR_FRAG_VAR
        );
        this.klass.addField(field);
        final Method getter = new Method("getFragment");
        getter.makeOverridden();
        getter.setReturnType(NodeConstructor.STR_FRAG_TYPE);
        getter.setCode("return this.fragment;");
        this.klass.addMethod(getter);
    }

    /**
     * Creates a field with the 'Fragment' type and setter for it.
     */
    protected void createFragmentWithSetter() {
        final Field field = new Field(
            NodeConstructor.STR_FRAG_BRIEF,
            NodeConstructor.STR_FRAG_TYPE,
            NodeConstructor.STR_FRAG_VAR
        );
        field.setInitExpr("EmptyFragment.INSTANCE");
        this.klass.addField(field);
        final Method setter = new Method("setFragment");
        setter.makeOverridden();
        setter.addArgument(NodeConstructor.STR_FRAG_TYPE, "obj");
        setter.setCode("this.fragment = obj;");
        this.klass.addMethod(setter);
    }

    /**
     * Creates the method 'getChild'.
     */
    protected void createChildrenGetter() {
        final Method getter = new Method("getChild");
        getter.makeOverridden();
        getter.addArgument(NodeConstructor.STR_INT, "index");
        getter.setReturnType("Node");
        getter.setCode("return this.children.get(index);");
        this.klass.addMethod(getter);
    }

    /**
     * Creates a class that implements the node type interface, as well as a static field
     * with an object of this class and a method to get this object.
     * @return The empty class constructor to be filled
     */
    protected Klass createTypeClass() {
        final Field field = new Field("The type", NodeConstructor.STR_TYPE, "TYPE");
        field.makePublic();
        field.makeStaticFinal();
        field.setInitExpr("new TypeImpl()");
        this.klass.addField(field);
        final Method getter = new Method("getType");
        getter.makeOverridden();
        getter.setReturnType(NodeConstructor.STR_TYPE);
        getter.setCode(String.format("return %s.TYPE;", this.rule.getType()));
        this.klass.addMethod(getter);
        final Klass subclass = new Klass(
            String.format("Type descriptor of the '%s' node", this.rule.getType()),
            "TypeImpl"
        );
        subclass.makePrivate();
        subclass.makeStatic();
        subclass.setInterfaces(NodeConstructor.STR_TYPE);
        this.klass.addClass(subclass);
        return subclass;
    }

    /**
     * Creates a class that implements the node builder interface.
     * @return The empty class constructor to be filled
     */
    protected Klass createBuilderClass() {
        final Klass subclass = new Klass(
            String.format("Class for '%s' node construction", this.rule.getType()),
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
        final List<String> hierarchy = this.getEnv().getHierarchy(this.getRule().getType());
        final StringBuilder init  = new StringBuilder(128);
        init.append(NodeConstructor.LIST_BEGIN);
        boolean separator = false;
        for (final String item : hierarchy) {
            if (separator) {
                init.append(NodeConstructor.SEPARATOR);
            }
            separator = true;
            init.append(ssg.getFieldName(item));
        }
        init.append(NodeConstructor.LIST_END);
        final Field field = new Field(
            "Hierarchy",
            NodeConstructor.LIST_STRING,
            "HIERARCHY"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(init.toString());
        this.klass.addField(field);
        final Method getter = new Method("getHierarchy");
        getter.setReturnType(NodeConstructor.LIST_STRING);
        getter.setCode("return TypeImpl.HIERARCHY;");
        this.klass.addMethod(getter);
    }

    /**
     * Creates a setter that does not accept any data.
     */
    protected void createNoDataSetter() {
        final Method setter = new Method("setData");
        setter.makeOverridden();
        setter.addArgument("String", "str");
        setter.setReturnType(NodeConstructor.STR_BOOLEAN);
        setter.setCode("return str.isEmpty();");
        this.klass.addMethod(setter);
    }
}
