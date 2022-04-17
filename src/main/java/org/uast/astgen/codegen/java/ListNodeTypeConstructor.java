/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Node;

/**
 * Generates type subclass source code for rules that describe list nodes.
 *
 * @since 1.0
 */
final class ListNodeTypeConstructor extends NodeConstructor {
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
    ListNodeTypeConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
        this.ssg = new StaticStringGenerator(klass);
    }

    @Override
    public void construct() {
        final Node rule = this.getRule();
        final Klass klass = this.getKlass();
        final Method name = new Method("getName");
        name.setReturnType("String");
        name.setCode(String.format("return %s;", this.ssg.getFieldName(rule.getType())));
        klass.addMethod(name);
        this.fillChildTypes();
        this.fillHierarchy(this.ssg);
        final Method builder = new Method("createBuilder");
        builder.setReturnType("Builder");
        builder.setCode("return new Constructor();");
        klass.addMethod(builder);
    }

    /**
     * Fills in everything related to the child types list.
     */
    private void fillChildTypes() {
        final Klass klass = this.getKlass();
        final Descriptor descriptor = (Descriptor) this.getRule().getComposition().get(0);
        final String type = descriptor.getType();
        final Field field = new Field(
            "The list of child types",
            ListNodeTypeConstructor.LIST_CHILD,
            "CHILDREN"
        );
        field.makePrivate();
        field.makeStaticFinal();
        final String init = String.format(
            "Collections.singletonList(new ChildDescriptor(%s, false))",
            this.ssg.getFieldName(type)
        );
        field.setInitExpr(init);
        klass.addField(field);
        final Method getter = new Method("getChildTypes");
        getter.setReturnType(ListNodeTypeConstructor.LIST_CHILD);
        getter.setCode("return TypeImpl.CHILDREN;");
        klass.addMethod(getter);
    }
}
