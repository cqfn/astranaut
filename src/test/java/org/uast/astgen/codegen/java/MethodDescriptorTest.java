/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MethodDescriptor} class.
 *
 * @since 1.0
 */
public class MethodDescriptorTest {
    /**
     * Creating simple header.
     */
    @Test
    public void voidHeaderWithoutArgs() {
        final MethodDescriptor descriptor = new MethodDescriptor();
        descriptor.setBriefDescription("This method does nothing");
        final String header = descriptor.genHeader(1);
        Assertions.assertEquals(
            "    /\u002a*\n     * This method does nothing.\n     */\n",
            header
        );
    }

    /**
     * Creating header for method that return something.
     */
    @Test
    public void nonVoidHeaderWithoutArgs() {
        final MethodDescriptor descriptor = new MethodDescriptor();
        descriptor.setBriefDescription("Returns something");
        descriptor.setReturnType("String", "Something");
        final String header = descriptor.genHeader(1);
        final String expected =
            "    /\u002a*\n     * Returns something.\n     * \u0040return Something\n     */\n";
        Assertions.assertEquals(expected, header);
    }

    /**
     * Creating header for method that has an argument.
     */
    @Test
    public void oneArgument() {
        final MethodDescriptor descriptor = new MethodDescriptor();
        descriptor.setBriefDescription("Calculates something");
        descriptor.addArgument("int", "val", "Value");
        final String header = descriptor.genHeader(1);
        final String expected =
            "    /\u002a*\n     * Calculates something.\n     * \u0040param val Value\n     */\n";
        Assertions.assertEquals(expected, header);
    }
}
