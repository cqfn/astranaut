/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.codegen.java;

import java.util.List;
import java.util.Locale;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Empty;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates builder subclass source code for rules that describe ordinary nodes.
 *
 * @since 1.0
 */
final class OrdinaryNodeBuilderConstructor extends NodeConstructor {
    /**
     * The 'boolean' string.
     */
    private static final String STR_BOOLEAN = "boolean";

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
    OrdinaryNodeBuilderConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        this.createFragmentWithSetter();
        this.createNoDataSetter();
        if (!this.getRule().isEmpty()) {
            this.fillChildren();
        }
        this.createSetterChildrenList();
        this.createValidator();
        this.createCreator();
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
            final String type = descriptor.getType();
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
        final Method method = new Method("setChildrenList");
        method.makeOverridden();
        method.addArgument("List<Node>", "list");
        method.setReturnType(OrdinaryNodeBuilderConstructor.STR_BOOLEAN);
        final StringBuilder code = new StringBuilder(256);
        if (rule.isEmpty()) {
            code.append("return list.isEmpty();");
        } else {
            code.append(this.fillSetterForNonEmptyChildrenList());
        }
        method.setCode(code.toString());
        this.getKlass().addMethod(method);
    }

    /**
     * Fills the body of the method 'setChildrenList' for non-empty
     *  children list.
     * @return Source code
     */
    private String fillSetterForNonEmptyChildrenList() {
        final Node rule = this.getRule();
        final List<Child> composition = rule.getComposition();
        this.createMagicNumber(
            "The maximum number of nodes",
            "MAX_NODE_COUNT",
            composition.size()
        );
        final StringBuilder code = new StringBuilder(256);
        final String first = "final Node[] mapping = new Node[Constructor.MAX_NODE_COUNT];\n";
        final String second = String.format(
            "final ChildrenMapper mapper =\n\tnew ChildrenMapper(%s.TYPE.getChildTypes());\n",
            this.getRule().getType()
        );
        final String third = "final boolean result = mapper.map(mapping, list);\n";
        code.append(first).append(second).append(third).append("if (result) { \n");
        int index = 0;
        for (final Child child : this.getRule().getComposition()) {
            final Descriptor descriptor = (Descriptor) child;
            final String variable = descriptor.getVariableName();
            final String magic = variable.toUpperCase(Locale.ENGLISH)
                .concat("_POS");
            this.createMagicNumber(
                String.format("The position of the '%s' field", descriptor.getVariableName()),
                magic,
                index
            );
            code.append(
                String.format(
                    "this.%s = (%s) mapping[Constructor.%s];\n",
                    variable,
                    descriptor.getType(),
                    magic
                )
            );
            index = index + 1;
        }
        code.append("}\nreturn result;");
        return code.toString();
    }

    /**
     * Creates the method 'isValid'.
     */
    private void createValidator() {
        final Method method = new Method("isValid");
        method.makeOverridden();
        method.setReturnType(OrdinaryNodeBuilderConstructor.STR_BOOLEAN);
        final StringBuilder code = new StringBuilder(64);
        code.append("return ");
        boolean flag = false;
        for (final Child child : this.getRule().getComposition()) {
            final Descriptor descriptor = (Descriptor) child;
            if (descriptor.equals(Empty.INSTANCE)) {
                break;
            } else if (descriptor.getAttribute() != DescriptorAttribute.OPTIONAL) {
                if (flag) {
                    code.append("\n\t&& ");
                }
                code.append(OrdinaryNodeBuilderConstructor.STR_THIS)
                    .append(descriptor.getVariableName())
                    .append(" != null");
                flag = true;
            }
        }
        if (!flag) {
            code.append("true");
        }
        code.append(OrdinaryNodeBuilderConstructor.STR_SEMICOLON);
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
        final String first;
        final boolean empty = this.getRule().isEmpty();
        if (empty) {
            first = "";
        } else {
            first = "if (!this.isValid()) { throw new IllegalStateException(); }\n";
        }
        final String second = String.format(
            "final %s node = new %s();\n",
            type,
            type
        );
        final String third = "node.fragment = this.fragment;\n";
        final StringBuilder next = new StringBuilder(256);
        if (!empty) {
            next.append(this.fillCreateNodeForNonEmptyChildrenList());
        }
        final StringBuilder code = new StringBuilder(256);
        code.append(first).append(second).append(third).append(next)
            .append("return node;\n");
        return code.toString();
    }

    /**
     * Fills variables assignment code in the body of the method 'createNode' for non-empty
     *  children list.
     * @return Source code
     */
    private String fillCreateNodeForNonEmptyChildrenList() {
        final StringBuilder fourth = new StringBuilder(128);
        final StringBuilder fifth = new StringBuilder(128);
        fourth.append("node.children = new ListUtils<Node>()\n\t.add(");
        boolean flag = false;
        for (final Child child : this.getRule().getComposition()) {
            final Descriptor descriptor = (Descriptor) child;
            final String var = descriptor.getVariableName();
            if (flag) {
                fourth.append(',');
            }
            flag = true;
            fourth
                .append("\n\t\t")
                .append(OrdinaryNodeBuilderConstructor.STR_THIS)
                .append(var);
            if (!descriptor.getTag().isEmpty()) {
                fifth.append("node.").append(var).append(" = this.").append(var)
                .append(OrdinaryNodeBuilderConstructor.STR_SEMICOLON);
            }
        }
        fourth.append("\n\t)\n\t.make();\n");
        return fourth.append(fifth).toString();
    }
}
