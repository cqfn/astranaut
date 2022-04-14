/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;

/**
 * Generates class source code for rules that describe nodes.
 *
 * @since 1.0
 */
final class NodeClassConstructor extends NodeConstructor {
    /**
     * The 'Type' string.
     */
    private static final String STR_TYPE = "Type";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeClassConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        this.fillType();
        this.fillBuilder();
    }

    /**
     * Fills in everything related to the type.
     */
    private void fillType() {
        final Field field = new Field("The type", NodeClassConstructor.STR_TYPE, "TYPE");
        final Klass klass = this.getKlass();
        final Node rule = this.getRule();
        field.makePublic();
        field.makeStaticFinal();
        field.setInitExpr("new TypeImpl()");
        klass.addField(field);
        final Method getter = new Method("getType");
        getter.makeOverridden();
        getter.setReturnType(NodeClassConstructor.STR_TYPE);
        getter.setCode(String.format("return %s.TYPE;", rule.getType()));
        klass.addMethod(getter);
        final Klass subclass = new Klass(
            String.format("Type descriptor of the '%s' node", rule.getType()),
            "TypeImpl"
        );
        subclass.makePrivate();
        subclass.makeStatic();
        subclass.setInterfaces(NodeClassConstructor.STR_TYPE);
        klass.addClass(subclass);
        new NodeTypeConstructor(this.getEnv(), rule, subclass).run();
    }

    /**
     * Fills in everything related to the builder.
     */
    private void fillBuilder() {
        final Klass klass = this.getKlass();
        final Node rule = this.getRule();
        final Klass subclass = new Klass(
            String.format("Class for '%s' node construction", rule.getType()),
            "Constructor"
        );
        subclass.makePublic();
        subclass.makeStatic();
        subclass.makeFinal();
        subclass.setInterfaces("Builder");
        klass.addClass(subclass);
        new NodeBuilderConstructor(this.getEnv(), rule, subclass).run();
    }
}
