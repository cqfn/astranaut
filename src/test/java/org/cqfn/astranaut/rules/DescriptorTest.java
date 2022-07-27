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

package org.cqfn.astranaut.rules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Descriptor} and {@link DescriptorFactory} classes.
 *
 * @since 1.0
 */
public class DescriptorTest {
    /**
     * Test name.
     */
    private static final String NAME = "Expression";

    /**
     * Test name.
     */
    private static final String LABEL = "alpha";

    /**
     * Testing factory with simple name.
     */
    @Test
    public void simpleName() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            DescriptorTest.NAME
        );
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals(DescriptorTest.NAME, descriptor.toString());
        Assertions.assertEquals(0, descriptor.getHoleNumber());
    }

    /**
     * Testing factory with tagged name.
     */
    @Test
    public void taggedName() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            "left"
        );
        factory.replaceType(DescriptorTest.NAME);
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals("left@Expression", descriptor.toString());
    }

    /**
     * Testing factory with string data.
     */
    @Test
    public void stringData() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            "literal"
        );
        factory.setData(new StringData("+"));
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals("literal<\"+\">", descriptor.toString());
    }

    /**
     * Testing factory with hole.
     */
    @Test
    public void hole() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            "numericLiteral"
        );
        factory.setData(new Hole(1, HoleAttribute.NONE));
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals("numericLiteral<#1>", descriptor.toString());
    }

    /**
     * Creating optional element.
     */
    @Test
    public void optional() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            DescriptorTest.NAME
        );
        factory.setAttribute(DescriptorAttribute.OPTIONAL);
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals("[Expression]", descriptor.toString());
    }

    /**
     * Creating element that is a list.
     */
    @Test
    public void list() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            DescriptorTest.NAME
        );
        factory.setAttribute(DescriptorAttribute.LIST);
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals("{Expression}", descriptor.toString());
    }

    /**
     * Creating element with parameters.
     */
    @Test
    public void twoParameters() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            "Addition"
        );
        final DescriptorFactory left = new DescriptorFactory("beta", "first");
        left.replaceType(DescriptorTest.NAME);
        factory.addParameter(left.createDescriptor());
        final DescriptorFactory right = new DescriptorFactory("gamma", "second");
        right.replaceType(DescriptorTest.NAME);
        factory.addParameter(right.createDescriptor());
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals(
            "Addition(first@Expression, second@Expression)",
            descriptor.toString()
        );
    }

    /**
     * Creating element with hole as a parameter.
     */
    @Test
    public void holeAsParameter() {
        final DescriptorFactory factory = new DescriptorFactory(
            DescriptorTest.LABEL,
            "simpleExpression"
        );
        factory.addParameter(new Hole(1, HoleAttribute.NONE));
        factory.addParameter(new Hole(2, HoleAttribute.NONE));
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals(
            "simpleExpression(#1, #2)",
            descriptor.toString()
        );
    }
}
