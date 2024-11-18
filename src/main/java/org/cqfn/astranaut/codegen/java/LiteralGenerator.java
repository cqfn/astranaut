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
import org.cqfn.astranaut.dsl.LiteralDescriptor;
import org.cqfn.astranaut.dsl.NodeDescriptor;

/**
 * Generator that creates source code for a literal, that is, a node that has data
 *  and no child nodes.
 * @since 1.0.0
 */
public final class LiteralGenerator extends NonAbstractNodeGenerator {
    /**
     * Descriptor on the basis of which the source code will be built.
     */
    private final LiteralDescriptor rule;

    /**
     * Constructor.
     * @param rule The rule that describes regular node
     */
    public LiteralGenerator(final LiteralDescriptor rule) {
        this.rule = rule;
    }

    @Override
    public NodeDescriptor getRule() {
        return this.rule;
    }

    @Override
    public void createSpecificEntitiesInNodeClass(final Klass klass) {
        final Field value = new Field(
            this.rule.getDataType(),
            "data",
            "Value of the node"
        );
        value.makePrivate();
        klass.addField(value);
        this.needCollectionsClass();
        final Method getter = new Method(
            this.rule.getDataType(),
            "getValue",
            "Returns the value of the node in native form"
        );
        getter.setReturnsDescription("Value of the node");
        getter.makePublic();
        getter.setBody("return this.data;");
        if (this.rule.getDataType().equals("boolean")) {
            getter.suppressWarning("PMD.BooleanGetMethodName");
        }
        klass.addMethod(getter);
        final Method list = new Method(Strings.TYPE_NODE_LIST, "getChildrenList");
        list.makePublic();
        list.setBody("return Collections.emptyList();");
        klass.addMethod(list);
    }

    @Override
    public String getDataGetterBody() {
        final String serializer = this.rule.getSerializer();
        final String body;
        if (serializer.isEmpty()) {
            body = "return this.data;";
        } else {
            body = String.format(
                "return %s;",
                this.rule.getSerializer().replace("#", "this.data")
            );
        }
        return body;
    }

    @Override
    public String getChildCountGetterBody() {
        return "return 0;";
    }

    @Override
    public String getChildGetterBody() {
        return "throw new IndexOutOfBoundsException();";
    }

    @Override
    public void createSpecificEntitiesInTypeClass(final ConstantStrings constants,
        final Klass klass) {
        this.getClass();
    }

    @Override
    public void createSpecificEntitiesInBuilderClass(final Klass klass) {
        final String initial = this.rule.getInitial();
        if (!initial.isEmpty()) {
            final Constructor ctor = klass.createConstructor();
            ctor.setBody(String.format("this.data = %s;", initial));
        }
        final Field value = new Field(
            this.rule.getDataType(),
            "data",
            "Value of the node to be created"
        );
        value.makePrivate();
        klass.addField(value);
    }

    @Override
    public String getDataSetterBody() {
        final List<String> lines = new ArrayList<>(2);
        final String parser = this.rule.getParser();
        final String exception = this.rule.getException();
        String ret = "true";
        if (parser.isEmpty()) {
            lines.add("this.data = value;");
        } else if (exception.isEmpty()) {
            lines.add(
                String.format(
                    "this.data = %s;",
                    parser.replace("#", "value")
                )
            );
        } else {
            ret = "result";
            lines.add("boolean result = true;");
            lines.add("try {");
            lines.add(
                String.format(
                    "this.data = %s;",
                    parser.replace("#", "value")
                )
            );
            lines.add(String.format("} catch (final %s ignored) {", exception));
            lines.add("result = false;");
            lines.add("}");
        }
        lines.add(String.format("return %s;", ret));
        return String.join("\n", lines);
    }

    @Override
    public String getChildrenListSetterBody() {
        return "return list.isEmpty();";
    }

    @Override
    public String getValidatorBody() {
        return "return true;";
    }

    @Override
    public void fillNodeCreator(final List<String> lines) {
        lines.add("node.data = this.data;");
    }
}
