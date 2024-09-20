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

import org.cqfn.astranaut.rules.Node;

/**
 * Constructs classes, fields and methods for rules that describe nodes.
 *
 * @since 0.1.5
 */
abstract class NodeConstructor extends BaseConstructor {
    /**
     * The 'int' string.
     */
    private static final String STR_INT = "int";

    /**
     * The 'boolean' string.
     */
    private static final String STR_BOOLEAN = "boolean";

    /**
     * The rule.
     */
    private final Node rule;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeConstructor(final Environment env, final Node rule, final Klass klass) {
        super(env, klass);
        this.rule = rule;
    }

    /**
     * Returns the rule.
     * @return The rule
     */
    protected Node getRule() {
        return this.rule;
    }

    @Override
    protected String getType() {
        return this.rule.getType();
    }

    /**
     * Creates the method 'getChild'.
     */
    protected void createChildrenGetter() {
        final Method getter = new Method("getChild");
        getter.makeOverridden();
        getter.addArgument(NodeConstructor.STR_INT, "index");
        getter.setReturnType("Node");
        if (this.rule.isEmpty()) {
            getter.setCode("throw new IndexOutOfBoundsException();");
        } else {
            getter.setCode("return this.children.get(index);");
        }
        this.getKlass().addMethod(getter);
    }

    /**
     * Creates a setter that does not accept any data.
     */
    protected void createNoDataSetter() {
        final Method setter = new Method("setData");
        setter.makeOverridden();
        setter.addArgument("String", "str");
        setter.setReturnType(NodeConstructor.STR_BOOLEAN);
        setter.setCode("return str.isEmpty();");
        this.getKlass().addMethod(setter);
    }

    /**
     * Creates field that contains magic number.
     * @param brief The brief description
     * @param name The name
     * @param value The value
     */
    protected void createMagicNumber(final String brief, final String name, final int value) {
        final Field field = new Field(
            brief,
            NodeConstructor.STR_INT,
            name
        );
        field.makeStaticFinal();
        field.setInitExpr(String.valueOf(value));
        this.getKlass().addField(field);
    }
}
