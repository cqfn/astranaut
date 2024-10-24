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

import org.cqfn.astranaut.dsl.ListNodeDescriptor;
import org.cqfn.astranaut.dsl.NodeDescriptor;

/**
 * Generator that creates source code for a list node, that is, node that can contain
 *  an unlimited number of child nodes of the same type.
 * @since 1.0.0
 */
public final class ListNodeGenerator extends NodeGenerator {
    /**
     * Descriptor on the basis of which the source code will be built.
     */
    private final ListNodeDescriptor rule;

    /**
     * Constructor.
     * @param rule The rule that describes regular node
     */
    public ListNodeGenerator(final ListNodeDescriptor rule) {
        this.rule = rule;
    }

    @Override
    public NodeDescriptor getRule() {
        return this.rule;
    }

    @Override
    public void createSpecificEntitiesInNodeClass(final Klass klass) {
        final String type = String.format("List<%s>", this.rule.getChildType());
        final Field children = new Field(
            type,
            "children",
            "List of child nodes"
        );
        children.makePrivate();
        klass.addField(children);
    }

    @Override
    public String getDataGetterBody() {
        return "return \"\";";
    }

    @Override
    public String getChildCountGetterBody() {
        return "return this.children.size();";
    }

    @Override
    public String getChildGetterBody() {
        return "return this.children.get(index);";
    }

    @Override
    public void createSpecificEntitiesInBuilderClass(final Klass klass) {
        this.needCollectionsClass();
        final String type = String.format("List<%s>", this.rule.getChildType());
        final Field children = new Field(
            type,
            "children",
            "List of child nodes"
        );
        children.makePrivate();
        klass.addField(children);
        final Constructor ctor = klass.createConstructor();
        ctor.makePublic();
        ctor.setBody("this.children = Collections.emptyList();");
    }

    @Override
    public String getDataSetterBody() {
        return "return value.isEmpty();";
    }

    @Override
    public String getChildrenListSetterBody() {
        return "return list.isEmpty();";
    }

    @Override
    public String getValidatorBody() {
        return "return true;";
    }
}
