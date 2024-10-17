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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Generator that creates source code for a regular node, that is, a node that may have
 *  a limited number of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeGenerator implements RuleGenerator {
    /**
     * The rule that describes regular node.
     */
    private final RegularNodeDescriptor rule;

    /**
     * Constructor.
     * @param rule The rule that describes regular node
     */
    public RegularNodeGenerator(final RegularNodeDescriptor rule) {
        this.rule = rule;
    }

    @Override
    public Set<CompilationUnit> createUnits(final Context context) {
        final String name = this.rule.getName();
        final String brief = String.format("Node of the '%s' type.", name);
        final Klass node = new Klass(name, brief);
        node.makePublic();
        node.makeFinal();
        node.setImplementsList("Node");
        node.setVersion(context.getVersion());
        node.addNested(this.createTypeClass(context));
        node.addNested(this.createBuilderClass(context));
        this.fillNodeClass(node);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            node
        );
        unit.addImport("java.util.List");
        final String base = "org.cqfn.astranaut.core.base.";
        unit.addImport(base.concat("Node"));
        unit.addImport(base.concat("Fragment"));
        unit.addImport(base.concat("Type"));
        unit.addImport(base.concat("Builder"));
        return Collections.singleton(unit);
    }

    /**
     * Fills the class describing the node with fields and methods.
     * @param node Class describing the node
     */
    private void fillNodeClass(final Klass node) {
        RegularNodeGenerator.createFragmentFieldAndGetter(node);
        this.createTypeFieldAndGetter(node);
        RegularNodeGenerator.createDataGetter(node);
        RegularNodeGenerator.createChildrenFieldAndGetter(node);
    }

    /**
     * Creates a field and a method related to the fragment.
     * @param klass Class describing the node
     */
    private static void createFragmentFieldAndGetter(final Klass klass) {
        final Field field = new Field(
            "Fragment",
            "fragment",
            "Fragment of source code that is associated with the node."
        );
        field.makePrivate();
        klass.addField(field);
        final Method getter = new Method("Fragment", "getFragment");
        getter.makePublic();
        getter.setBody("return this.fragment;");
        klass.addMethod(getter);
    }

    /**
     * Creates fields and a method related to the type.
     * @param klass Class describing the node
     */
    private void createTypeFieldAndGetter(final Klass klass) {
        final String name = this.rule.getName();
        final Field typename = new Field("String", "NAME", "Name of the type");
        typename.makePublic();
        typename.makeStatic();
        typename.makeFinal(String.format("\"%s\"", name));
        klass.addField(typename);
        final Field object = new Field("Type", "TYPE", "Type of the node");
        object.makePublic();
        object.makeStatic();
        object.makeFinal(String.format("new %sType()", name));
        klass.addField(object);
        final Method getter = new Method("Type", "getType");
        getter.makePublic();
        getter.setBody(String.format("return %s.TYPE;", name));
        klass.addMethod(getter);
    }

    /**
     * Creates the 'getData()' method.
     * @param klass Class describing the node
     */
    private static void createDataGetter(final Klass klass) {
        final Method method = new Method("String", "getData");
        method.makePublic();
        method.setBody("return \"\";");
        klass.addMethod(method);
    }

    /**
     * Generates fields and methods related to children.
     * @param node Class describing the node
     */
    private static void createChildrenFieldAndGetter(final Klass node) {
        final Method count = new Method("int", "getChildCount");
        count.makePublic();
        count.setBody("return 0;");
        node.addMethod(count);
        final Method getter = new Method("Node", "getChild");
        getter.makePublic();
        getter.addArgument("int", "index");
        getter.setBody("throw new IndexOutOfBoundsException();");
        node.addMethod(getter);
    }

    /**
     * Creates a nested class that implements the node type.
     * @param context Context
     * @return Class description
     */
    private Klass createTypeClass(final Context context) {
        final String name = this.rule.getName();
        final Klass klass = new Klass(
            String.format("%sType", name),
            String.format("Type implementation describing '%s' nodes.", name)
        );
        klass.makePrivate();
        klass.makeStatic();
        klass.makeFinal();
        klass.setImplementsList("Type");
        klass.setVersion(context.getVersion());
        this.createNameGetter(klass);
        this.createBuilderCreator(klass);
        return klass;
    }

    /**
     * Creates the 'getName()' method.
     * @param klass Class describing the type
     */
    private void createNameGetter(final Klass klass) {
        final Method method = new Method("String", "getName");
        method.makePublic();
        method.setBody(String.format("return %s.NAME;", this.rule.getName()));
        klass.addMethod(method);
    }

    /**
     * Creates the 'createBuilder()' method.
     * @param klass Class describing the node
     */
    private void createBuilderCreator(final Klass klass) {
        final Method method = new Method("Builder", "createBuilder");
        method.makePublic();
        method.setBody(String.format("return new %s.Constructor();", this.rule.getName()));
        klass.addMethod(method);
    }

    /**
     * Creates a nested class that implements the node builder.
     * @param context Context
     * @return Class description
     */
    private Klass createBuilderClass(final Context context) {
        final String name = this.rule.getName();
        final Klass klass = new Klass(
            String.format("Constructor"),
            String.format("Constructor (builder) that creates nodes of the '%s' type.", name)
        );
        klass.makePublic();
        klass.makeStatic();
        klass.makeFinal();
        klass.setImplementsList("Builder");
        klass.setVersion(context.getVersion());
        RegularNodeGenerator.createFragmentFieldAndSetter(klass);
        RegularNodeGenerator.createDataSetter(klass);
        RegularNodeGenerator.createChildrenListSetter(klass);
        RegularNodeGenerator.createValidator(klass);
        this.createNodeCreator(klass);
        return klass;
    }

    /**
     * Creates a field and a method related to the fragment.
     * @param klass Class describing the builder
     */
    private static void createFragmentFieldAndSetter(final Klass klass) {
        final Field field = new Field(
            "Fragment",
            "fragment",
            "Fragment of source code that is associated with the node."
        );
        field.makePrivate();
        klass.addField(field);
        final Method setter = new Method("void", "setFragment");
        setter.makePublic();
        setter.addArgument("Fragment", "object");
        setter.setBody("this.fragment = object;");
        klass.addMethod(setter);
    }

    /**
     * Creates the 'setData()' method.
     * @param klass Class describing the builder
     */
    private static void createDataSetter(final Klass klass) {
        final Method method = new Method("boolean", "setData");
        method.makePublic();
        method.addArgument("String", "value");
        method.setBody("return value.isEmpty();");
        klass.addMethod(method);
    }

    /**
     * Creates the 'setChildrenList()' method.
     * @param klass Class describing the builder
     */
    private static void createChildrenListSetter(final Klass klass) {
        final Method method = new Method("boolean", "setChildrenList");
        method.makePublic();
        method.addArgument("List<Node>", "list");
        method.setBody("return list.isEmpty();");
        klass.addMethod(method);
    }

    /**
     * Creates the 'isValid()' method.
     * @param klass Class describing the builder
     */
    private static void createValidator(final Klass klass) {
        final Method method = new Method("boolean", "isValid");
        method.makePublic();
        method.setBody("return true;");
        klass.addMethod(method);
    }

    /**
     * Creates the 'createNode()' method.
     * @param klass Class describing the builder
     */
    private void createNodeCreator(final Klass klass) {
        final Method method = new Method("Node", "createNode");
        method.makePublic();
        final String name = this.rule.getName();
        final List<String> lines = new ArrayList<>();
        lines.add(String.format("final %s node = new %s();", name, name));
        lines.add("node.fragment = this.fragment;");
        lines.add("return node;");
        method.setBody(String.join("\n", lines));
        klass.addMethod(method);
    }
}
