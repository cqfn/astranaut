/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.rules;

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
        factory.replaceName(DescriptorTest.NAME);
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
        factory.setData(new Hole(1));
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
        left.replaceName(DescriptorTest.NAME);
        factory.addParameter(left.createDescriptor());
        final DescriptorFactory right = new DescriptorFactory("gamma", "second");
        right.replaceName(DescriptorTest.NAME);
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
        factory.addParameter(new Hole(1));
        factory.addParameter(new Hole(2));
        final Descriptor descriptor = factory.createDescriptor();
        Assertions.assertEquals(
            "simpleExpression(#1, #2)",
            descriptor.toString()
        );
    }
}
