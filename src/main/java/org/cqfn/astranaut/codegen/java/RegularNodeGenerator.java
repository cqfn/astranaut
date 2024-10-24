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

import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Generator that creates source code for a regular node, that is, a node that may have
 *  a limited number of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeGenerator extends NodeGenerator {
    /**
     * Descriptor on the basis of which the source code will be built.
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
    public NodeDescriptor getRule() {
        return this.rule;
    }

    @Override
    public void createSpecificEntitiesInNodeClass(final Klass klass) {
        if (this.rule.getExtChildTypes().isEmpty()) {
            this.needCollectionsClass();
            final Method list = new Method("List<Node>", "getChildrenList");
            list.makePublic();
            list.setBody("return Collections.emptyList();");
            klass.addMethod(list);
        }
    }

    @Override
    public String getDataGetterBody() {
        return "return \"\";";
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
    public void createSpecificEntitiesInBuilderClass(final Klass klass) {
        this.getClass();
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
