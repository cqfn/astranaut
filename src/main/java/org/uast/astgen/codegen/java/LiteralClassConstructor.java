/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Literal;

/**
 * Generates class source code for rules that describe ordinary nodes.
 *
 * @since 1.0
 */
final class LiteralClassConstructor extends LiteralConstructor {
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
    LiteralClassConstructor(final Environment env, final Literal rule, final Klass klass) {
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
    }

    /**
     * Fills in everything related to the type.
     */
    private void fillType() {
        final Klass subtype = this.createTypeClass();
        new LiteralTypeConstructor(this.getEnv(), this.getRule(), subtype).run();
    }

    /**
     * Fills in everything related to the builder.
     */
    private void fillBuilder() {
        final Klass subtype = this.createBuilderClass();
        new LiteralBuilderConstructor(this.getEnv(), this.getRule(), subtype).run();
    }

    /**
     * Creates, common fields and getters for them.
     */
    private void createCommonFields() {
        final Klass klass = this.getKlass();
        final Literal rule = this.getRule();
        klass.addField(new Field("The data", rule.getKlass(), "data"));
        final Method data = new Method("getData");
        data.makeOverridden();
        data.setReturnType("String");
        data.setCode(
            String.format(
                "return %s;",
                rule.getStringifier().replace("#", "this.value")
            )
        );
        klass.addMethod(data);
        final Method count = new Method("getChildCount");
        count.makeOverridden();
        count.setReturnType(LiteralClassConstructor.STR_INT);
        count.setCode("return 0;");
        klass.addMethod(count);
        final Method getter = new Method("getChild");
        getter.makeOverridden();
        getter.addArgument(LiteralClassConstructor.STR_INT, "index");
        getter.setReturnType("Node");
        getter.setCode("throw new IndexOutOfBoundsException();");
        klass.addMethod(getter);
    }
}
