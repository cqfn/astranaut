/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;

/**
 * Generates class source code for rules that describe list nodes.
 *
 * @since 1.0
 */
final class ListNodeClassConstructor extends NodeConstructor {
    /**
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    ListNodeClassConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        final Constructor ctor = new Constructor(this.getRule().getType());
        ctor.makePrivate();
        this.getKlass().addConstructor(ctor);
        this.fillType();
        this.fillBuilder();
        this.createFragmentWithGetter();
        this.createCommonFields();
        this.createChildrenGetter();
    }

    /**
     * Fills in everything related to the type.
     */
    private void fillType() {
        this.createTypeClass();
    }

    /**
     * Fills in everything related to the builder.
     */
    private void fillBuilder() {
        this.createBuilderClass();
    }

    /**
     * Creates, common fields and getters for them.
     */
    private void createCommonFields() {
        final Klass klass = this.getKlass();
        final Node rule = this.getRule();
        final Method data = new Method("getData");
        data.makeOverridden();
        data.setReturnType("String");
        data.setCode("return \"\";");
        klass.addMethod(data);
        klass.addField(
            new Field(
                "List of child nodes",
                String.format("List<%s>", rule.getType()),
                "children"
            )
        );
        final Method count = new Method("getChildCount");
        count.makeOverridden();
        count.setReturnType(ListNodeClassConstructor.STR_INT);
        if (rule.hasOptionalChild()) {
            count.setCode("return this.children.size();");
        } else {
            final Field num = new Field(
                "The number of children",
                ListNodeClassConstructor.STR_INT,
                "CHILD_COUNT"
            );
            num.makeStaticFinal();
            num.setInitExpr(String.valueOf(rule.getComposition().size()));
            klass.addField(num);
            count.setCode(String.format("return %s.CHILD_COUNT;", rule.getType()));
        }
        klass.addMethod(count);
    }
}
