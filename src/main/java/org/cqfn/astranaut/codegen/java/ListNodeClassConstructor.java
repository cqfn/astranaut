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

import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates class source code for rules that describe list nodes.
 *
 * @since 0.1.5
 */
final class ListNodeClassConstructor extends NodeConstructor {
    /**
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    ListNodeClassConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        final Constructor ctor = new Constructor(this.getRule().getType());
        ctor.makePrivate();
        this.getKlass().addConstructor(ctor);
        this.fillType();
        this.fillBuilder();
        this.createFragmentWithGetter();
        this.createCommonFields();
        this.createChildrenGetter();
    }

    /**
     * Fills in everything related to the type.
     */
    private void fillType() {
        final Klass subclass = this.createTypeClass();
        new ListNodeTypeConstructor(this.getEnv(), this.getRule(), subclass).run();
    }

    /**
     * Fills in everything related to the builder.
     */
    private void fillBuilder() {
        final Klass subclass = this.createBuilderClass();
        new ListNodeBuilderConstructor(this.getEnv(), this.getRule(), subclass).run();
    }

    /**
     * Creates, common fields and getters for them.
     */
    private void createCommonFields() {
        final Klass klass = this.getKlass();
        final Node rule = this.getRule();
        final Descriptor descriptor = (Descriptor) rule.getComposition().get(0);
        final String type = descriptor.getType();
        final Method data = new Method("getData");
        data.makeOverridden();
        data.setReturnType("String");
        data.setCode("return \"\";");
        klass.addMethod(data);
        klass.addField(
            new Field(
                "List of child nodes",
                String.format("List<%s>", type),
                "children"
            )
        );
        final Method count = new Method("getChildCount");
        count.makeOverridden();
        count.setReturnType(ListNodeClassConstructor.STR_INT);
        count.setCode("return this.children.size();");
        klass.addMethod(count);
        final Method getter = new Method(
            String.format("Return a child node with '%s' type by its index", type),
            String.format("get%s", type)
        );
        getter.addArgument(ListNodeClassConstructor.STR_INT, "index", "Child index");
        getter.setReturnType(type, "A node");
        getter.setCode("return this.children.get(index);");
        klass.addMethod(getter);
    }
}
