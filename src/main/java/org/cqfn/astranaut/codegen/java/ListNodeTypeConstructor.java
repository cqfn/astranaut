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

import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates type subclass source code for rules that describe list nodes.
 *
 * @since 0.1.5
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
        getter.setCode(String.format("return %s.CHILDREN;", klass.getName()));
        klass.addMethod(getter);
    }
}
