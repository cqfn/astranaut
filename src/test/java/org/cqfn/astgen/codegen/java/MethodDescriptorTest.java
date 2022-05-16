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
        final MethodDescriptor descriptor = new MethodDescriptor(
            "This method does nothing",
            "first"
        );
        final String header = descriptor.generateHeader(1);
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
        final MethodDescriptor descriptor = new MethodDescriptor(
            "Returns something",
            "second"
        );
        descriptor.setReturnType("String", "Something");
        final String header = descriptor.generateHeader(1);
        final String expected =
            "    /\u002a*\n     * Returns something.\n     * \u0040return Something\n     */\n";
        Assertions.assertEquals(expected, header);
    }

    /**
     * Creating header for method that has an argument.
     */
    @Test
    public void oneArgument() {
        final MethodDescriptor descriptor = new MethodDescriptor(
            "Calculates something",
            "third"
        );
        descriptor.addArgument("int", "val", "Value");
        final String header = descriptor.generateHeader(1);
        final String expected =
            "    /\u002a*\n     * Calculates something.\n     * \u0040param val Value\n     */\n";
        Assertions.assertEquals(expected, header);
    }

    /**
     * Generating method signature for interface.
     */
    @Test
    public void signatureIface() {
        final MethodDescriptor descriptor = new MethodDescriptor(
            "Calculates something else",
            "calculate"
        );
        descriptor.addArgument("float", "num", "Number");
        final String result = descriptor.generateSignature(true);
        final String expected = "void calculate(float num)";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Generating method signature for method.
     */
    @Test
    public void signatureMethod() {
        final MethodDescriptor descriptor = new MethodDescriptor(
            "Finds the maximum",
            "max"
        );
        final String type = "double";
        descriptor.setReturnType(type, "Result");
        descriptor.addArgument(type, "aaa", "First value");
        descriptor.addArgument(type, "bbb", "Second value");
        final String result = descriptor.generateSignature(false);
        final String expected = "double max(final double aaa, final double bbb)";
        Assertions.assertEquals(expected, result);
    }

    /**
     * Testing the whole descriptor.
     */
    @Test
    public void wholeDescriptor() {
        final MethodDescriptor descriptor = new MethodDescriptor(
            "Kernel panic",
            "panic"
        );
        final String result = descriptor.generate(1);
        final String expected =
            "    /\u002a*\n     * Kernel panic.\n     */\n    void panic();\n";
        Assertions.assertEquals(expected, result);
    }
}
