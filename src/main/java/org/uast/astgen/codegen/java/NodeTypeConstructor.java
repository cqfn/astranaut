/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.rules.Node;

/**
 * Generates type subclass source code for rules that describe nodes.
 *
 * @since 1.0
 */
final class NodeTypeConstructor extends NodeConstructor {
    /**
     * The 'List&lt;String&gt;' type.
     */
    private static final String LIST_STRING = "List<String>";

    /**
     * The 'List&lt;ChildDescriptor&gt;' type.
     */
    private static final String LIST_CHILD = "List<ChildDescriptor>";

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
     * Static string generator to avoid Qulice error messages.
     */
    private final StaticStringGenerator ssg;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeTypeConstructor(final Environment env, final Node rule, final Klass klass) {
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
        this.fillHierarchy();
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
        this.createChildrenField();
        final Method getter = new Method("getChildTypes");
        getter.setReturnType(NodeTypeConstructor.LIST_CHILD);
        getter.setCode("return TypeImpl.CHILDREN;");
        klass.addMethod(getter);
    }

    /**
     * Creates 'List&lt;ChildDescriptor&gt;' CHILDREN structure.
     */
    private void createChildrenField() {
        final Klass klass = this.getKlass();
        final StringBuilder init = new StringBuilder(128);
        init.append(NodeTypeConstructor.LIST_BEGIN);
        boolean flag = false;
        for (final Child child : this.getRule().getComposition()) {
            assert child instanceof Descriptor;
            final Descriptor descriptor = (Descriptor) child;
            if (flag) {
                init.append(NodeTypeConstructor.SEPARATOR);
            }
            flag = true;
            boolean optional = false;
            if (descriptor.getAttribute() == DescriptorAttribute.LIST) {
                optional = true;
            }
            init.append("new ChildDescriptor(")
                .append(this.ssg.getFieldName(descriptor.getName()))
                .append(NodeTypeConstructor.SEPARATOR)
                .append(optional)
                .append(')');
        }
        init.append(NodeTypeConstructor.LIST_END);
        final Field field = new Field(
            "The list of child types",
            NodeTypeConstructor.LIST_CHILD,
            "CHILDREN"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(init.toString());
        klass.addField(field);
    }

    /**
     * Fills in everything related to the hierarchy.
     */
    private void fillHierarchy() {
        final Klass klass = this.getKlass();
        final List<String> hierarchy = this.getEnv().getHierarchy(this.getRule().getType());
        final StringBuilder init  = new StringBuilder(128);
        init.append(NodeTypeConstructor.LIST_BEGIN);
        boolean flag = false;
        for (final String item : hierarchy) {
            if (flag) {
                init.append(NodeTypeConstructor.SEPARATOR);
            }
            flag = true;
            init.append(this.ssg.getFieldName(item));
        }
        init.append(NodeTypeConstructor.LIST_END);
        final Field field = new Field("Hierarchy", NodeTypeConstructor.LIST_STRING, "HIERARCHY");
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(init.toString());
        klass.addField(field);
        final Method getter = new Method("getHierarchy");
        getter.setReturnType(NodeTypeConstructor.LIST_STRING);
        getter.setCode("return TypeImpl.HIERARCHY;");
        klass.addMethod(getter);
    }
}
