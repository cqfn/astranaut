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
import java.util.Locale;
import java.util.Set;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.NodeDescriptor;

/**
 * Generator that creates compilation units that describe a non-abstract node.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class NonAbstractNodeGenerator implements RuleGenerator {
    /**
     * Flag indicating that the 'java.util.Collections' class should be included
     *  in the generated code.
     */
    private boolean collections;

    /**
     * Flag indicating that the 'java.util.ArrayList' class should be included
     *  in the generated code.
     */
    private boolean arraylist;

    /**
     * Flag indicating that the 'java.util.Arrays' class should be included
     *  in the generated code.
     */
    private boolean arrays;

    /**
     * Flag indicating that the 'org.cqfn.astranaut.core.utils.ListUtils' class should be included
     *  in the generated code.
     */
    private boolean listutils;

    /**
     * Flag indicating that a non-trivial validator has been generated.
     */
    private boolean validator;

    /**
     * Flag indicating that the 'org.cqfn.astranaut.core.base.ChildDescriptor' class
     *  should be included in the generated code.
     */
    private boolean chldecr;

    /**
     * Flag indicating that the 'org.cqfn.astranaut.core.algorithms.NodeAllocator' class
     *  should be included in the generated code.
     */
    private boolean allocator;

    @Override
    public final Set<CompilationUnit> createUnits(final Context context) {
        final String name = this.getRule().getName();
        final String brief = String.format("Node of the '%s' type.", name);
        final Klass klass = new Klass(name, brief);
        klass.makePublic();
        klass.makeFinal();
        final List<AbstractNodeDescriptor> bases = this.getRule().getBaseDescriptors();
        if (bases.isEmpty()) {
            klass.setImplementsList(Strings.TYPE_NODE);
        } else {
            klass.setImplementsList(
                bases.stream()
                    .map(AbstractNodeDescriptor::getName)
                    .toArray(String[]::new)
            );
        }
        klass.setVersion(context.getVersion());
        klass.addNested(this.createTypeClass(context));
        klass.addNested(this.createBuilderClass(context));
        this.fillNodeClass(klass);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            klass
        );
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        if (this.collections) {
            unit.addImport("java.util.Collections");
        }
        if (this.arraylist) {
            unit.addImport("java.util.ArrayList");
        }
        if (this.arrays) {
            unit.addImport("java.util.Arrays");
        }
        if (this.listutils) {
            unit.addImport("org.cqfn.astranaut.core.utils.ListUtils");
        }
        if (this.allocator) {
            unit.addImport("org.cqfn.astranaut.core.algorithms.NodeAllocator");
        }
        final String base = "org.cqfn.astranaut.core.base.";
        unit.addImport(base.concat(Strings.TYPE_NODE));
        unit.addImport(base.concat(Strings.TYPE_FRAGMENT));
        unit.addImport(base.concat(Strings.TYPE_TYPE));
        unit.addImport(base.concat(Strings.TYPE_BUILDER));
        if (this.chldecr) {
            unit.addImport(base.concat(Strings.TYPE_CHLD_DESCR));
        }
        return Collections.singleton(unit);
    }

    /**
     * Returns descriptor on the basis of which the source code will be built.
     * @return Node descriptor
     */
    public abstract NodeDescriptor getRule();

    /**
     * Creates specific entities in the class describing the node.
     * @param klass Class describing the node
     */
    public abstract void createSpecificEntitiesInNodeClass(Klass klass);

    /**
     * Returns body of the 'getData()' method.
     * @return Body of the 'getData()' method
     */
    public abstract String getDataGetterBody();

    /**
     * Returns body of the 'getChildCount()' method.
     * @return Body of the 'getChildCount()' method
     */
    public abstract String getChildCountGetterBody();

    /**
     * Returns body of the 'getChild()' method.
     * @return Body of the 'getChild()' method
     */
    public abstract String getChildGetterBody();

    /**
     * Creates specific entities in the class describing the type of the node.
     * @param constants Generator of fields containing constant (final) strings.
     * @param klass Class describing the node type
     */
    public abstract void createSpecificEntitiesInTypeClass(ConstantStrings constants, Klass klass);

    /**
     * Creates specific entities in the class describing the builder of the node.
     * @param klass Class describing the node builder
     */
    public abstract void createSpecificEntitiesInBuilderClass(Klass klass);

    /**
     * Returns body of the 'setData()' method.
     * @return Body of the 'setData()' method
     */
    public abstract String getDataSetterBody();

    /**
     * Returns body of the 'setChildrenList()' method.
     * @return Body of the 'setChildrenList()' method
     */
    public abstract String getChildrenListSetterBody();

    /**
     * Returns body of the 'isValid()' method.
     * @return Body of the 'isValid()' method
     */
    public abstract String getValidatorBody();

    /**
     * Fills the body of 'createNode' method.
     * @param lines List of where to write source code lines
     */
    public abstract void fillNodeCreator(List<String> lines);

    /**
     * Sets the flag indicating that the 'java.util.Collections' class
     *  should be included in the generated code.
     */
    protected void needCollectionsClass() {
        this.collections = true;
    }

    /**
     * Sets the flag indicating that the 'java.util.ArrayList' class
     *  should be included in the generated code.
     */
    protected void needArrayListClass() {
        this.arraylist = true;
    }

    /**
     * Sets the flag indicating that the 'java.util.Arrays' class should be included
     *  in the generated code.
     */
    protected void needArraysClass() {
        this.arrays = true;
    }

    /**
     * Sets the flag indicating that the 'org.cqfn.astranaut.core.utils.ListUtils' class
     *  should be included in the generated code.
     */
    protected void needListUtilsClass() {
        this.listutils = true;
    }

    /**
     * Sets a flag indicating that a non-trivial validator has been generated.
     */
    protected void hasNonTrivialValidator() {
        this.validator = true;
    }

    /**
     * Sets the flag indicating that the 'org.cqfn.astranaut.core.base.ChildDescriptor' class
     *  should be included in the generated code.
     */
    protected void needChildDescriptorClass() {
        this.chldecr = true;
    }

    /**
     * Sets the flag indicating that the 'org.cqfn.astranaut.core.algorithms.NodeAllocator' class
     *  should be included in the generated code.
     */
    protected void needNodeAllocatorClass() {
        this.allocator = true;
    }

    /**
     * Fills the class describing the node with fields and methods.
     * @param klass Class describing the node
     */
    private void fillNodeClass(final Klass klass) {
        NonAbstractNodeGenerator.createFragmentFieldAndGetter(klass);
        this.createTypeFieldAndGetter(klass);
        this.createDataGetter(klass);
        this.createChildrenFieldAndGetter(klass);
        this.createSpecificEntitiesInNodeClass(klass);
    }

    /**
     * Creates a field and a method related to the fragment.
     * @param klass Class describing the node
     */
    private static void createFragmentFieldAndGetter(final Klass klass) {
        final Field field = new Field(
            Strings.TYPE_FRAGMENT,
            "fragment",
            "Fragment of source code that is associated with the node."
        );
        field.makePrivate();
        klass.addField(field);
        final Method getter = new Method(Strings.TYPE_FRAGMENT, "getFragment");
        getter.makePublic();
        getter.setBody("return this.fragment;");
        klass.addMethod(getter);
    }

    /**
     * Creates fields and a method related to the type.
     * @param klass Class describing the node
     */
    private void createTypeFieldAndGetter(final Klass klass) {
        final String name = this.getRule().getName();
        final Field typename = new Field(Strings.TYPE_STRING, "NAME", "Name of the type");
        typename.makePublic();
        typename.makeStatic();
        typename.makeFinal(String.format("\"%s\"", name));
        klass.addField(typename);
        final Field object = new Field(Strings.TYPE_TYPE, "TYPE", "Type of the node");
        object.makePublic();
        object.makeStatic();
        object.makeFinal(String.format("new %sType()", name));
        klass.addField(object);
        final Method getter = new Method(Strings.TYPE_TYPE, "getType");
        getter.makePublic();
        getter.setBody(String.format("return %s.TYPE;", name));
        klass.addMethod(getter);
    }

    /**
     * Creates the 'getData()' method.
     * @param klass Class describing the node
     */
    private void createDataGetter(final Klass klass) {
        final Method method = new Method(Strings.TYPE_STRING, "getData");
        method.makePublic();
        method.setBody(this.getDataGetterBody());
        klass.addMethod(method);
    }

    /**
    * Generates fields and methods related to children.
    * @param klass Class describing the node
    */
    private void createChildrenFieldAndGetter(final Klass klass) {
        final Method count = new Method(Strings.TYPE_INT, "getChildCount");
        count.makePublic();
        count.setBody(this.getChildCountGetterBody());
        klass.addMethod(count);
        final Method getter = new Method(Strings.TYPE_NODE, "getChild");
        getter.makePublic();
        getter.addArgument(Strings.TYPE_INT, "index");
        getter.setBody(this.getChildGetterBody());
        klass.addMethod(getter);
    }

    /**
     * Creates a nested class that implements the node type.
     * @param context Context
     * @return Class description
     */
    private Klass createTypeClass(final Context context) {
        final String name = this.getRule().getName();
        final Klass klass = new Klass(
            String.format("%sType", name),
            String.format("Type implementation describing '%s' nodes.", name)
        );
        klass.makePrivate();
        klass.makeStatic();
        klass.makeFinal();
        klass.setImplementsList(Strings.TYPE_TYPE);
        klass.setVersion(context.getVersion());
        final ConstantStrings constants = new ConstantStrings(
            klass,
            "TYPE",
            "The '#' type name"
        );
        this.createSpecificEntitiesInTypeClass(constants, klass);
        this.createNameGetter(klass);
        this.createHierarchyFieldAndGetter(constants, klass);
        this.createPropertiesGetter(klass);
        this.createBuilderCreator(klass);
        return klass;
    }

    /**
     * Creates the 'getName()' method.
     * @param klass Class describing the type
     */
    private void createNameGetter(final Klass klass) {
        final Method method = new Method(Strings.TYPE_STRING, "getName");
        method.makePublic();
        method.setBody(String.format("return %s.NAME;", this.getRule().getName()));
        klass.addMethod(method);
    }

    /**
     * Creates the 'HIERARCHY' field and 'getHierarchy()' method.
     * @param constants Generator of fields containing constant (final) strings.
     * @param klass Class describing the type
     */
    private void createHierarchyFieldAndGetter(final ConstantStrings constants, final Klass klass) {
        final List<NodeDescriptor> topology = this.getRule().getTopology();
        final Field field = new Field(
            Strings.TYPE_LIST_STRINGS,
            "HIERARCHY",
            "Node hierarchy"
        );
        field.makePrivate();
        field.makeStatic();
        if (topology.size() > 1) {
            this.needArraysClass();
            final StringBuilder builder = new StringBuilder(128);
            builder.append("Arrays.asList(");
            final List<String> hierarchy = new ArrayList<>(1);
            hierarchy.add(String.format("%s.NAME", this.getRule().getName()));
            for (int index = 1; index < topology.size(); index = index + 1) {
                hierarchy.add(constants.createStaticField(topology.get(index).getName()));
            }
            builder.append(String.join(", ", hierarchy)).append(')');
            field.makeFinal(builder.toString());
        } else {
            this.needCollectionsClass();
            field.makeFinal(
                String.format(
                    "Collections.singletonList(%s.NAME)",
                    this.getRule().getName()
                )
            );
        }
        klass.addField(field);
        final Method getter = new Method(Strings.TYPE_LIST_STRINGS, "getHierarchy");
        getter.makePublic();
        getter.setBody(String.format("return %sType.HIERARCHY;", this.getRule().getName()));
        klass.addMethod(getter);
    }

    /**
     * Creates the 'getProperties()' method.
     * @param klass Class describing the type
     */
    private void createPropertiesGetter(final Klass klass) {
        final Method method = new Method(Strings.TYPE_MAP_STRINGS, "getProperties");
        String language = this.getRule().getLanguage();
        if (language.isEmpty()) {
            language = "common";
        }
        method.makePublic();
        method.setBody(
            String.format(
                "return %s%sFactory.PROPERTIES;",
                language.substring(0, 1).toUpperCase(Locale.ENGLISH),
                language.substring(1)
            )
        );
        klass.addMethod(method);
    }

    /**
     * Creates the 'createBuilder()' method.
     * @param klass Class describing the node
     */
    private void createBuilderCreator(final Klass klass) {
        final Method method = new Method(Strings.TYPE_BUILDER, "createBuilder");
        method.makePublic();
        method.setBody(String.format("return new %s.Constructor();", this.getRule().getName()));
        klass.addMethod(method);
    }

    /**
     * Creates a nested class that implements the node builder.
     * @param context Context
     * @return Class description
     */
    private Klass createBuilderClass(final Context context) {
        final String name = this.getRule().getName();
        final Klass klass = new Klass(
            "Constructor",
            String.format("Constructor (builder) that creates nodes of the '%s' type.", name)
        );
        klass.makePublic();
        klass.makeStatic();
        klass.makeFinal();
        klass.setImplementsList(Strings.TYPE_BUILDER);
        klass.setVersion(context.getVersion());
        NonAbstractNodeGenerator.createFragmentFieldAndSetter(klass);
        this.createSpecificEntitiesInBuilderClass(klass);
        this.createDataSetter(klass);
        this.createChildrenListSetter(klass);
        this.createValidator(klass);
        this.createNodeCreator(klass);
        return klass;
    }

    /**
     * Creates a field and a method related to the fragment.
     * @param klass Class describing the builder
     */
    private static void createFragmentFieldAndSetter(final Klass klass) {
        final Field field = new Field(
            Strings.TYPE_FRAGMENT,
            "fragment",
            "Fragment of source code that is associated with the node."
        );
        field.makePrivate();
        klass.addField(field);
        final Method setter = new Method(Strings.TYPE_VOID, "setFragment");
        setter.makePublic();
        setter.addArgument(Strings.TYPE_FRAGMENT, "object");
        setter.setBody("this.fragment = object;");
        klass.addMethod(setter);
    }

    /**
     * Creates the 'setData()' method.
     * @param klass Class describing the builder
     */
    private void createDataSetter(final Klass klass) {
        final Method method = new Method(Strings.TYPE_BOOLEAN, "setData");
        method.makePublic();
        method.addArgument(Strings.TYPE_STRING, "value");
        method.setBody(this.getDataSetterBody());
        klass.addMethod(method);
    }

    /**
     * Creates the 'setChildrenList()' method.
     * @param klass Class describing the builder
     */
    private void createChildrenListSetter(final Klass klass) {
        final Method method = new Method(Strings.TYPE_BOOLEAN, "setChildrenList");
        method.makePublic();
        method.addArgument(Strings.TYPE_NODE_LIST, "list");
        method.setBody(this.getChildrenListSetterBody());
        klass.addMethod(method);
    }

    /**
     * Creates the 'isValid()' method.
     * @param klass Class describing the builder
     */
    private void createValidator(final Klass klass) {
        final Method method = new Method(Strings.TYPE_BOOLEAN, "isValid");
        method.makePublic();
        method.setBody(this.getValidatorBody());
        klass.addMethod(method);
    }

    /**
     * Creates the 'createNode()' method.
     * @param klass Class describing the builder
     */
    private void createNodeCreator(final Klass klass) {
        final Method method = new Method(Strings.TYPE_NODE, "createNode");
        method.makePublic();
        final String name = this.getRule().getName();
        final List<String> lines = new ArrayList<>(16);
        if (this.validator) {
            lines.add("if (!this.isValid()) {");
            lines.add("throw new IllegalStateException();");
            lines.add("}");
        }
        lines.add(String.format("final %s node = new %s();", name, name));
        lines.add("node.fragment = this.fragment;");
        this.fillNodeCreator(lines);
        lines.add("return node;");
        method.setBody(String.join("\n", lines));
        klass.addMethod(method);
    }
}
