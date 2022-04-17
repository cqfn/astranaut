/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Literal;

/**
 * Generates type subclass source code for rules that describe ordinary nodes.
 *
 * @since 1.0
 */
final class LiteralTypeConstructor extends LiteralConstructor {
    /**
     * The {@code List<ChildDescriptor>} type.
     */
    private static final String LIST_CHILD = "List<ChildDescriptor>";

    /**
     * Static string generator to avoid Qulice error messages.
     */
    private final StaticStringGenerator ssg;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    LiteralTypeConstructor(final Environment env, final Literal rule, final Klass klass) {
        super(env, rule, klass);
        this.ssg = new StaticStringGenerator(klass);
    }

    @Override
    public void construct() {
        final Literal rule = this.getRule();
        final Klass klass = this.getKlass();
        final Method name = new Method("getName");
        name.setReturnType("String");
        name.setCode(String.format("return %s;", this.ssg.getFieldName(rule.getType())));
        klass.addMethod(name);
        final Method getter = new Method("getChildTypes");
        getter.setReturnType(LiteralTypeConstructor.LIST_CHILD);
        getter.setCode("return Collections.emptyList();");
        klass.addMethod(getter);
        this.fillHierarchy(this.ssg);
        final Method builder = new Method("createBuilder");
        builder.setReturnType("Builder");
        builder.setCode("return new Constructor();");
        klass.addMethod(builder);
    }
}
