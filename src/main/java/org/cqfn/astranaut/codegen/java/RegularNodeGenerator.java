/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.cqfn.astranaut.dsl.ChildDescriptorExt;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Generator that creates source code for a regular node, that is, a node that may have
 *  a limited number of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeGenerator extends NonAbstractNodeGenerator {
    /**
     * Descriptor on the basis of which the source code will be built.
     */
    private final RegularNodeDescriptor rule;

    /**
     * Names of variables that store child nodes.
     */
    private final String[] names;

    /**
     * Flag indicating that this node is a wrapper (decorator) of another node.
     */
    private final boolean decorator;

    /**
     * Constructor.
     * @param rule The rule that describes regular node
     */
    public RegularNodeGenerator(final RegularNodeDescriptor rule) {
        this.rule = rule;
        this.names = RegularNodeGenerator.generateVariableNames(rule);
        this.decorator = rule.getExtChildTypes().size() == 1
            && !rule.getExtChildTypes().get(0).isOptional();
    }

    @Override
    public NodeDescriptor getRule() {
        return this.rule;
    }

    @Override
    public void createSpecificEntitiesInNodeClass(final Klass klass) {
        final Method list = new Method(Strings.TYPE_NODE_LIST, "getChildrenList");
        list.makePublic();
        if (this.rule.getExtChildTypes().isEmpty()) {
            this.needCollectionsClass();
            list.setBody("return Collections.emptyList();");
        } else {
            list.setBody("return this.children;");
            this.createFieldsWithGettersForTaggedChildren(klass);
            final Field children = new Field(
                Strings.TYPE_NODE_LIST,
                "children",
                "List of child nodes"
            );
            children.makePrivate();
            klass.addField(children);
        }
        klass.addMethod(list);
    }

    @Override
    public String getDataGetterBody() {
        return "return \"\";";
    }

    @Override
    public String getChildCountGetterBody() {
        final String body;
        if (this.names.length == 0) {
            body = "return 0;";
        } else {
            body = "return this.children.size();";
        }
        return body;
    }

    @Override
    public String getChildGetterBody() {
        final String body;
        if (this.names.length == 0) {
            body = "throw new IndexOutOfBoundsException();";
        } else {
            body = "return this.children.get(index);";
        }
        return body;
    }

    @Override
    public void createSpecificEntitiesInTypeClass(final ConstantStrings constants,
        final Klass klass) {
        if (this.names.length > 0) {
            final String lsttype = "List<ChildDescriptor>";
            this.needChildDescriptorClass();
            final Field types = new Field(
                lsttype,
                "CHILD_TYPES",
                "List of child node descriptors"
            );
            types.makePrivate();
            types.makeStatic();
            final StringBuilder initial = new StringBuilder();
            initial.append("ChildDescriptor.create()");
            for (final ChildDescriptorExt descriptor : this.rule.getExtChildTypes()) {
                if (descriptor.isOptional()) {
                    initial.append(".optional(");
                } else {
                    initial.append(".required(");
                }
                final String field = constants.createStaticField(descriptor.getType());
                initial.append(field).append(')');
            }
            initial.append(".build()");
            types.makeFinal(initial.toString());
            klass.addField(types);
            final Method getter = new Method(lsttype, "getChildTypes");
            getter.makePublic();
            getter.setBody(String.format("return %s.CHILD_TYPES;", klass.getName()));
            klass.addMethod(getter);
        }
    }

    @Override
    public void createSpecificEntitiesInBuilderClass(final Klass klass) {
        if (this.names.length > 0) {
            this.createFieldsWithSettersForTaggedChildren(klass);
        }
    }

    @Override
    public String getDataSetterBody() {
        return "return value.isEmpty();";
    }

    @Override
    public String getChildrenListSetterBody() {
        final List<ChildDescriptorExt> children = this.rule.getExtChildTypes();
        final List<String> lines = new ArrayList<>(1);
        if (this.names.length == 0) {
            lines.add("return list.isEmpty();");
        } else if (this.decorator) {
            final String type = children.get(0).getType();
            lines.add("boolean result = false;");
            lines.add(
                String.format(
                    "if (list.size() == 1 && list.get(0) instanceof %s) {",
                    type
                )
            );
            lines.add(String.format("this.%s = (%s) list.get(0);", this.names[0], type));
            lines.add("result = true;");
            lines.add("}");
            lines.add("return result;");
        } else {
            this.needNodeAllocatorClass();
            lines.add(
                String.format(
                    "final NodeAllocator allocator = new NodeAllocator(%sType.CHILD_TYPES);",
                    this.rule.getName()
                )
            );
            lines.add(
                String.format(
                    "final Node[] nodes = new Node[%d];",
                    children.size()
                )
            );
            lines.add("final boolean result = allocator.allocate(nodes, list);");
            lines.add("if (result) {");
            for (int index = 0; index < this.names.length; index = index + 1) {
                lines.add(
                    String.format(
                        "this.%s = (%s) nodes[%d];",
                        this.names[index],
                        children.get(index).getType(),
                        index
                    )
                );
            }
            lines.add("}");
            lines.add("return result;");
        }
        return String.join("\n", lines);
    }

    @Override
    public String getValidatorBody() {
        boolean flag = false;
        final StringBuilder builder = new StringBuilder(80);
        final List<ChildDescriptorExt> children = this.rule.getExtChildTypes();
        for (int index = 0; index < children.size(); index = index + 1) {
            final ChildDescriptorExt descriptor = children.get(index);
            if (descriptor.isOptional()) {
                continue;
            }
            if (flag) {
                builder.append(" && ");
            }
            flag = true;
            builder.append("this.").append(this.names[index]).append(" != null");
        }
        final String expression;
        if (flag) {
            expression = builder.toString();
            this.hasNonTrivialValidator();
        } else {
            expression = "true";
        }
        return String.format("return %s;", expression);
    }

    @Override
    public void fillNodeCreator(final List<String> lines) {
        final List<ChildDescriptorExt> children = this.rule.getExtChildTypes();
        for (int index = 0; index < children.size(); index = index + 1) {
            final ChildDescriptorExt descriptor = children.get(index);
            if (!descriptor.getTag().isEmpty()) {
                lines.add(
                    String.format(
                        "node.%s = this.%s;",
                        this.names[index],
                        this.names[index]
                    )
                );
            }
        }
        if (this.decorator) {
            this.needCollectionsClass();
            lines.add(
                String.format(
                    "node.children = Collections.singletonList(this.%s);",
                    this.names[0]
                )
            );
        } else if (this.names.length > 0) {
            this.needListUtilsClass();
            final StringBuilder builder = new StringBuilder();
            for (int index = 0; index < this.names.length; index = index + 1) {
                if (index > 0) {
                    builder.append(", ");
                }
                builder.append("this.").append(this.names[index]);
            }
            lines.add(
                String.format(
                    "node.children = new ListUtils<Node>().add(%s).make();",
                    builder.toString()
                )
            );
        }
    }

    /**
     * Creates fields ans getters for all tagged children.
     * @param klass Class describing regular node
     */
    private void createFieldsWithGettersForTaggedChildren(final Klass klass) {
        final List<ChildDescriptorExt> children = this.rule.getExtChildTypes();
        int count = 0;
        for (int index = 0; index < children.size(); index = index + 1) {
            final ChildDescriptorExt descriptor = children.get(index);
            final String tag = descriptor.getTag();
            if (tag.isEmpty()) {
                continue;
            }
            count = count + 1;
            final Field field = new Field(
                descriptor.getType(),
                this.names[index],
                String.format("Child node with '%s' tag", tag)
            );
            field.makePrivate();
            klass.addField(field);
            final Method getter = new Method(
                descriptor.getType(),
                String.format(
                    "get%s%s",
                    tag.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    tag.substring(1)
                ),
                String.format("Returns child node with '%s' tag", tag)
            );
            getter.makePublic();
            if (descriptor.isOptional()) {
                getter.setReturnsDescription(
                    String.format(
                        "Child node or {@code null} if the node with '%s' tag is not specified",
                        tag
                    )
                );
            } else {
                getter.setReturnsDescription("Child node (can't be {@code null})");
            }
            getter.setBody(String.format("return this.%s;", this.names[index]));
            klass.addMethod(getter);
        }
        if (count > 1) {
            klass.suppressWarning("PMD.DataClass");
        }
    }

    /**
     * Creates fields and setters for all tagged children.
     * @param klass Class describing builder of regular node
     */
    private void createFieldsWithSettersForTaggedChildren(final Klass klass) {
        final List<ChildDescriptorExt> children = this.rule.getExtChildTypes();
        for (int index = 0; index < children.size(); index = index + 1) {
            final ChildDescriptorExt descriptor = children.get(index);
            final String tag = descriptor.getTag();
            final String name = this.names[index];
            final String brief;
            if (tag.isEmpty()) {
                brief = String.format(
                    "%s%s node",
                    name.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    name.substring(1)
                );
            } else {
                brief = String.format("Child node with '%s' tag", tag);
            }
            final Field field = new Field(
                descriptor.getType(),
                name,
                brief
            );
            field.makePrivate();
            klass.addField(field);
            if (tag.isEmpty()) {
                continue;
            }
            final Method setter = new Method(
                Strings.TYPE_VOID,
                String.format(
                    "set%s%s",
                    tag.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    tag.substring(1)
                ),
                String.format("Sets child node with '%s' tag", tag)
            );
            setter.makePublic();
            setter.addArgument(descriptor.getType(), "object", "Child node");
            if (descriptor.isOptional()) {
                setter.setBody(String.format("this.%s = object;", tag.toLowerCase(Locale.ENGLISH)));
            } else {
                setter.setBody(
                    String.format(
                        "if (object != null) { this.%s = object; }",
                        tag.toLowerCase(Locale.ENGLISH)
                    )
                );
            }
            klass.addMethod(setter);
        }
    }

    /**
     * Generates variable names that store child nodes.
     * @param rule Descriptor on the basis of which the source code will be built
     * @return Array of variable names.
     */
    private static String[] generateVariableNames(final RegularNodeDescriptor rule) {
        final List<ChildDescriptorExt> children = rule.getExtChildTypes();
        final int size = children.size();
        final String[] names = new String[size];
        final NameGenerator generator = new NameGenerator();
        for (int index = 0; index < size; index = index + 1) {
            final ChildDescriptorExt child = children.get(index);
            final String tag = child.getTag();
            String name = generator.nextName();
            if (size == 1) {
                name = "child";
            }
            if (!tag.isEmpty()) {
                name = tag.toLowerCase(Locale.ENGLISH);
            }
            names[index] = name;
        }
        return names;
    }
}
