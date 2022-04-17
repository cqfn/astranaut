/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;

/**
 * Constructs classes, fields and methods for rules that describe nodes.
 *
 * @since 1.0
 */
abstract class NodeConstructor extends BaseConstructor {
    /**
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * The 'int' string.
     */
    private static final String STR_BOOLEAN = "boolean";

    /**
     * The rule.
     */
    private final Node rule;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, klass);
        this.rule = rule;
    }

    /**
     * Returns the rule.
     * @return The rule
     */
    protected Node getRule() {
        return this.rule;
    }

    @Override
    protected String getType() {
        return this.rule.getType();
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
        this.getKlass().addMethod(getter);
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
        this.getKlass().addMethod(setter);
    }
}
