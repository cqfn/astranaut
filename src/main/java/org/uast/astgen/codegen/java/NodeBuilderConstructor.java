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
 * Generates builder subclass source code for rules that describe nodes.
 *
 * @since 1.0
 */
final class NodeBuilderConstructor extends NodeConstructor {
    /**
     * The 'boolean' string.
     */
    private static final String STR_BOOLEAN = "boolean";

    /**
     * The 'Fragment' string.
     */
    private static final String STR_FRAGMENT = "Fragment";

    /**
     * The 'this.' string.
     */
    private static final String STR_THIS = "this.";

    /**
     * The ';\n' string.
     */
    private static final String STR_SEMICOLON = ";\n";

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
        this.fillChildren();
        this.createSetterChildrenList();
        this.createValidator();
        this.createCreator();
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
        setter.setReturnType(NodeBuilderConstructor.STR_BOOLEAN);
        setter.setCode("return str.isEmpty();");
        klass.addMethod(setter);
    }

    /**
     * Fills in everything related to the child nodes.
     */
    private void fillChildren() {
        final Klass klass = this.getKlass();
        int index = 0;
        for (final Child child : this.getRule().getComposition()) {
            assert child instanceof Descriptor;
            final Descriptor descriptor = (Descriptor) child;
            final String brief;
            final String tag = descriptor.getTag();
            final String type = descriptor.getName();
            final String variable = descriptor.getVariableName();
            if (tag.isEmpty()) {
                brief = String.format("Node %d", index);
            } else {
                brief = String.format("Node with the '%s' tag", tag);
                final Method setter = new Method(
                    String.format("Sets the node with the '%s' tag", tag),
                    String.format("set%s", descriptor.getTagCapital())
                );
                setter.addArgument(type, "node", "The node");
                setter.setCode(String.format("this.%s = node;", variable));
                klass.addMethod(setter);
            }
            final Field field = new Field(brief, type, variable);
            klass.addField(field);
            index = index + 1;
        }
    }

    /**
     * Creates the method 'setChildrenList'.
     */
    private void createSetterChildrenList() {
        final Node rule = this.getRule();
        final List<Child> composition = rule.getComposition();
        final Method method = new Method("setChildrenList");
        method.makeOverridden();
        method.addArgument("List<Node>", "list");
        final StringBuilder code = new StringBuilder(256);
        final String first = String.format(
            "final Node[] mapping = new Node[%d];\n",
            composition.size()
        );
        final String second = String.format(
            "final ChildrenMapper mapper = new ChildrenMapper(%s.TYPE.getChildTypes());\n",
            rule.getType()
        );
        final String third = "final boolean result = mapper.map(mapping, list);\n";
        code.append(first).append(second).append(third).append("if result { \n");
        int index = 0;
        for (final Child child : this.getRule().getComposition()) {
            final Descriptor descriptor = (Descriptor) child;
            code.append(
                String.format(
                    "this.%s = (%s) mapping[%d];\n",
                    descriptor.getVariableName(),
                    descriptor.getName(),
                    index
                )
            );
            index = index + 1;
        }
        code.append("}\nreturn result;");
        method.setCode(code.toString());
        this.getKlass().addMethod(method);
    }

    /**
     * Creates the method 'isValid'.
     */
    private void createValidator() {
        final Method method = new Method("isValid");
        method.makeOverridden();
        method.setReturnType(NodeBuilderConstructor.STR_BOOLEAN);
        final StringBuilder code = new StringBuilder(64);
        code.append("return ");
        boolean flag = false;
        for (final Child child : this.getRule().getComposition()) {
            final Descriptor descriptor = (Descriptor) child;
            if (descriptor.getAttribute() != DescriptorAttribute.OPTIONAL) {
                if (flag) {
                    code.append("\n\t&& ");
                }
                code.append(NodeBuilderConstructor.STR_THIS)
                    .append(descriptor.getVariableName())
                    .append(" != null");
                flag = true;
            }
        }
        if (!flag) {
            code.append("true");
        }
        code.append(NodeBuilderConstructor.STR_SEMICOLON);
        method.setCode(code.toString());
        this.getKlass().addMethod(method);
    }

    /**
     * Creates the method 'createNode'.
     */
    private void createCreator() {
        final Method method = new Method("createNode");
        method.makeOverridden();
        method.setReturnType(this.getRule().getType());
        method.setCode(this.prepareCreatorCode());
        this.getKlass().addMethod(method);
    }

    /**
     * Prepared code for the 'createNode' method.
     * @return Source code
     */
    private String prepareCreatorCode() {
        final String type = this.getRule().getType();
        final String first = "if (!this.isValid()) { throw new IllegalStateException(); }\n";
        final String second = String.format(
            "final %s node = new %s();\n",
            type,
            type
        );
        final String third = "node.fragment = this.fragment;\n";
        final StringBuilder fourth = new StringBuilder(64);
        final StringBuilder fifth = new StringBuilder(128);
        fourth.append("node.children = Arrays.asList(");
        boolean flag = false;
        for (final Child child : this.getRule().getComposition()) {
            final Descriptor descriptor = (Descriptor) child;
            final String var = descriptor.getVariableName();
            if (flag) {
                fourth.append(", ");
            }
            flag = true;
            fourth.append(NodeBuilderConstructor.STR_THIS).append(var);
            fifth.append("node.").append(var).append(" = this.").append(var)
                .append(NodeBuilderConstructor.STR_SEMICOLON);
        }
        fourth.append(");\n");
        final StringBuilder code = new StringBuilder(256);
        code.append(first).append(second).append(third).append(fourth).append(fifth)
            .append("return node;\n");
        return code.toString();
    }
}
