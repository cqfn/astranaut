/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;

/**
 * Generates builder subclass source code for rules that describe nodes.
 *
 * @since 1.0
 */
final class NodeBuilderConstructor extends NodeConstructor {
    /**
     * The 'Fragment' string.
     */
    private static final String STR_FRAGMENT = "Fragment";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeBuilderConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        this.fillFragment();
        this.fillData();
    }

    /**
     * Fills in everything related to the fragment.
     */
    private void fillFragment() {
        final Klass klass = this.getKlass();
        final Field field = new Field(
            "The fragment associated with the node",
            NodeBuilderConstructor.STR_FRAGMENT,
            "fragment"
        );
        field.setInitExpr("EmptyFragment.INSTANCE");
        klass.addField(field);
        final Method setter = new Method("setFragment");
        setter.makeOverridden();
        setter.addArgument(NodeBuilderConstructor.STR_FRAGMENT, "obj");
        setter.setCode("this.fragment = obj;");
        klass.addMethod(setter);
    }

    /**
     * Fills in everything related to the data.
     */
    private void fillData() {
        final Klass klass = this.getKlass();
        final Method setter = new Method("setData");
        setter.makeOverridden();
        setter.addArgument("String", "str");
        setter.setReturnType("boolean");
        setter.setCode("return str.isEmpty();");
        klass.addMethod(setter);
    }
}
