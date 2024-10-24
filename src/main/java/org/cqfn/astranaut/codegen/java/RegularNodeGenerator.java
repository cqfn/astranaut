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

import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Generator that creates source code for a regular node, that is, a node that may have
 *  a limited number of some child nodes and no data.
 * @since 1.0.0
 */
public final class RegularNodeGenerator extends NodeGenerator {
    /**
     * Constructor.
     * @param rule The rule that describes regular node
     */
    public RegularNodeGenerator(final RegularNodeDescriptor rule) {
        super(rule);
    }

    @Override
    public String getDataGetterBody(final Klass klass) {
        return "return \"\";";
    }

    @Override
    public String getChildCountGetterBody(final Klass klass) {
        return "return 0;";
    }

    @Override
    public String getChildGetterBody(final Klass klass) {
        return "throw new IndexOutOfBoundsException();";
    }

    @Override
    public String getDataSetterBody(final Klass klass) {
        return "return value.isEmpty();";
    }

    @Override
    public String getChildrenListSetterBody(final Klass klass) {
        return "return list.isEmpty();";
    }

    @Override
    public String getValidatorBody(final Klass klass) {
        return "return true;";
    }
}
