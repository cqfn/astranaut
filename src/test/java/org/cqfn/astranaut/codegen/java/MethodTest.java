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

package org.cqfn.astranaut.codegen.java;

import java.io.IOException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Method} class.
 *
 * @since 0.1.5
 */
class MethodTest {
    /**
     * Typical method name for tests.
     */
    private static final String METHOD_NAME = "test";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing code generation with a very simple method.
     */
    @Test
    void simpleMethod() {
        final Method method = new Method("Prints test string", MethodTest.METHOD_NAME);
        method.setCode("System.out.print(\"Ok.\");");
        final String expected = this.readTest("simple_method.txt");
        final String actual = method.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation with a private method.
     */
    @Test
    void simplePrivateMethod() {
        final Method method = new Method("Prints another test string", MethodTest.METHOD_NAME);
        method.setCode("System.out.print(\"Hello )\");");
        method.makePrivate();
        final String expected = this.readTest("simple_private_method.txt");
        final String actual = method.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation for method with arguments and return value.
     */
    @Test
    void methodWithArgsAndReturn() {
        final Method method = new Method("Finds the sum of two numbers", MethodTest.METHOD_NAME);
        final String type = "int";
        method.addArgument(type, "aaa", "First number");
        method.addArgument(type, "bbb", "Second number");
        method.setReturnType(type, "The sum");
        method.setCode("return aaa + bbb;");
        final String expected = this.readTest("method_with_args_and_return.txt");
        final String actual = method.generate(1);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation for method that contains 'if' statement.
     */
    @Test
    void methodWithIfStatement() {
        final Method method = new Method(
            "Finds the maximum of two numbers",
            MethodTest.METHOD_NAME
        );
        final String type = "float";
        method.addArgument(type, "left", "Left number");
        method.addArgument(type, "right", "Right number");
        method.setReturnType(type, "The maximum");
        final String code =
            "final float ret;\nif (left > right) {ret = left;} else {ret = right;}\nreturn ret;";
        method.setCode(code);
        final String expected = this.readTest("method_with_if_statement.txt");
        final String actual = method.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation with overridden method.
     */
    @Test
    void overriddenMethod() {
        final Method method = new Method("Represents the object as a string", "toString");
        method.setReturnType("String", "A string");
        method.setCode("return \"test\";");
        method.makeOverridden();
        final String expected = this.readTest("overridden_method.txt");
        final String actual = method.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation with abstract method.
     */
    @Test
    void abstractMethod() {
        final Method method = new Method("Associates new data with the object", "setData");
        method.addArgument("Data", "data", "The new data");
        method.makeAbstract();
        final String expected = this.readTest("abstract_method.txt");
        final String actual = method.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Reads test source from the file.
     * @param name The file name
     * @return Test source
     */
    private String readTest(final String name) {
        String result = "";
        boolean oops = false;
        try {
            result = new FilesReader(MethodTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
