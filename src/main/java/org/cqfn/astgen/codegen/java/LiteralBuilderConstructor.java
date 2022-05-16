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
package org.cqfn.astgen.codegen.java;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astgen.rules.Literal;

/**
 * Generates builder subclass source code for rules that describe ordinary nodes.
 *
 * @since 1.0
 */
final class LiteralBuilderConstructor extends LiteralConstructor {
    /**
     * The 'boolean' string.
     */
    private static final String STR_BOOLEAN = "boolean";

    /**
     * The 'value' string.
     */
    private static final String STR_VALUE = "value";

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    LiteralBuilderConstructor(final Environment env, final Literal rule, final Klass klass) {
        super(env, rule, klass);
    }

    @Override
    public void construct() {
        this.createFragmentWithSetter();
        this.fillData();
        this.createSetterChildrenList();
        this.createValidator();
        this.createCreator();
    }

    /**
     * Creates the data field and setter for it.
     */
    public void fillData() {
        final Klass klass = this.getKlass();
        final Literal rule = this.getRule();
        klass.addField(
            new Field(
                "The flag indicating that the builder has been initialized",
                LiteralBuilderConstructor.STR_BOOLEAN,
                "initialized"
            )
        );
        klass.addField(
            new Field(
                "The data",
                rule.getKlass(),
                "data"
            )
        );
        final Method method = new Method("setData");
        method.makeOverridden();
        method.setReturnType(LiteralBuilderConstructor.STR_BOOLEAN);
        method.addArgument("String", LiteralBuilderConstructor.STR_VALUE);
        final String exception = rule.getException();
        final List<String> code = new LinkedList<>();
        if (!exception.isEmpty()) {
            code.add("boolean success = true;\ntry {");
        }
        code.add(
            String.format(
                "this.data = %s;",
                rule.getParser().replace("#", LiteralBuilderConstructor.STR_VALUE)
            )
        );
        code.add("this.initialized = true;");
        if (exception.isEmpty()) {
            code.add("return true;");
        } else {
            code.add(String.format("} catch (final %s ignored) {", exception));
            code.add("success = false;\n}\nreturn success;");
        }
        method.setCode(String.join("\n", code));
        klass.addMethod(method);
    }

    /**
     * Creates the method 'setChildrenList'.
     */
    private void createSetterChildrenList() {
        final Method method = new Method("setChildrenList");
        method.makeOverridden();
        method.addArgument("List<Node>", "list");
        method.setReturnType(LiteralBuilderConstructor.STR_BOOLEAN);
        method.setCode("return list.isEmpty();");
        this.getKlass().addMethod(method);
    }

    /**
     * Creates the method 'isValid'.
     */
    private void createValidator() {
        final Method method = new Method("isValid");
        method.makeOverridden();
        method.setReturnType(LiteralBuilderConstructor.STR_BOOLEAN);
        method.setCode("return this.initialized;");
        this.getKlass().addMethod(method);
    }

    /**
     * Creates the method 'createNode'.
     */
    private void createCreator() {
        final Method method = new Method("createNode");
        method.makeOverridden();
        final String type = this.getRule().getType();
        method.setReturnType(type);
        final List<String> code = Arrays.asList(
            String.format("final %s node = new %s();", type, type),
            "node.fragment = this.fragment;",
            "node.data = this.data;",
            "return node;"
        );
        method.setCode(String.join("\n", code));
        this.getKlass().addMethod(method);
    }
}
