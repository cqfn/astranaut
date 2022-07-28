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

import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates type subclass source code for rules that describe ordinary nodes.
 *
 * @since 0.1.5
 */
final class OrdinaryNodeTypeConstructor extends NodeConstructor {
    /**
     * The {@code List<ChildDescriptor>} type.
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
    OrdinaryNodeTypeConstructor(final Environment env, final Node rule, final Klass klass) {
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
        this.fillProperties();
        final Method builder = new Method("createBuilder");
        builder.setReturnType("Builder");
        builder.setCode("return new Constructor();");
        klass.addMethod(builder);
    }

    /**
     * Fills in everything related to the child types list.
     */
    private void fillChildTypes() {
        final boolean empty = this.getRule().isEmpty();
        final Klass klass = this.getKlass();
        if (!empty) {
            this.createChildrenField();
        }
        final Method getter = new Method("getChildTypes");
        getter.setReturnType(OrdinaryNodeTypeConstructor.LIST_CHILD);
        if (empty) {
            getter.setCode("return Collections.emptyList();");
        } else {
            getter.setCode("return TypeImpl.CHILDREN;");
        }
        klass.addMethod(getter);
    }

    /**
     * Creates {@code List<ChildDescriptor>} CHILDREN structure.
     */
    private void createChildrenField() {
        final Klass klass = this.getKlass();
        final StringBuilder init = new StringBuilder(128);
        init.append(OrdinaryNodeTypeConstructor.LIST_BEGIN);
        boolean flag = false;
        for (final Child child : this.getRule().getComposition()) {
            assert child instanceof Descriptor;
            final Descriptor descriptor = (Descriptor) child;
            if (flag) {
                init.append(OrdinaryNodeTypeConstructor.SEPARATOR);
            }
            flag = true;
            boolean optional = false;
            if (descriptor.getAttribute() == DescriptorAttribute.OPTIONAL) {
                optional = true;
            }
            init.append("new ChildDescriptor(")
                .append(this.ssg.getFieldName(descriptor.getType()))
                .append(OrdinaryNodeTypeConstructor.SEPARATOR)
                .append(optional)
                .append(')');
        }
        init.append(OrdinaryNodeTypeConstructor.LIST_END);
        final Field field = new Field(
            "The list of child types",
            OrdinaryNodeTypeConstructor.LIST_CHILD,
            "CHILDREN"
        );
        field.makePrivate();
        field.makeStaticFinal();
        field.setInitExpr(init.toString());
        klass.addField(field);
    }
}
