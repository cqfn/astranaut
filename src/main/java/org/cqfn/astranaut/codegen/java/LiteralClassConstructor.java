/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import org.cqfn.astranaut.rules.Literal;

/**
 * Generates class source code for rules that describe ordinary nodes.
 *
 * @since 0.1.5
 */
final class LiteralClassConstructor extends LiteralConstructor {
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
    LiteralClassConstructor(final Environment env, final Literal rule, final Klass klass) {
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
    }

    /**
     * Fills in everything related to the type.
     */
    private void fillType() {
        final Klass subtype = this.createTypeClass();
        new LiteralTypeConstructor(this.getEnv(), this.getRule(), subtype).run();
    }

    /**
     * Fills in everything related to the builder.
     */
    private void fillBuilder() {
        final Klass subtype = this.createBuilderClass();
        new LiteralBuilderConstructor(this.getEnv(), this.getRule(), subtype).run();
    }

    /**
     * Creates, common fields and getters for them.
     */
    private void createCommonFields() {
        final Klass klass = this.getKlass();
        final Literal rule = this.getRule();
        klass.addField(new Field("The data", rule.getKlass(), "data"));
        final Method data = new Method("getData");
        data.makeOverridden();
        data.setReturnType("String");
        data.setCode(
            String.format(
                "return %s;",
                rule.getStringifier().replace("#", "this.data")
            )
        );
        klass.addMethod(data);
        final Method count = new Method("getChildCount");
        count.makeOverridden();
        count.setReturnType(LiteralClassConstructor.STR_INT);
        count.setCode("return 0;");
        klass.addMethod(count);
        final Method getter = new Method("getChild");
        getter.makeOverridden();
        getter.addArgument(LiteralClassConstructor.STR_INT, "index");
        getter.setReturnType("Node");
        getter.setCode("throw new IndexOutOfBoundsException();");
        klass.addMethod(getter);
    }
}
