/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.utils.FilesReader;

/**
 * Tests for {@link Method} class.
 *
 * @since 1.0
 */
public class MethodTest {
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
    public void simpleMethod() {
        final Method method = new Method(MethodTest.METHOD_NAME, "Prints test string");
        method.setCode("System.out.print(\"Ok.\");");
        final String expected = this.readTest("simple_method.txt");
        final String actual = method.generate(0);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation with a private method.
     */
    @Test
    public void simplePrivateMethod() {
        final Method method = new Method(MethodTest.METHOD_NAME, "Prints another test string");
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
    public void methodWithArgsAndReturn() {
        final Method method = new Method(MethodTest.METHOD_NAME, "Finds the sum of two numbers");
        final String type = "int";
        method.addArgument(type, "aaa", "First number");
        method.addArgument(type, "bbb", "Second number");
        method.setReturnType(type, "The sum");
        method.setCode("return aaa + bbb;");
        final String expected = this.readTest("method_with_args_and_return.txt");
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
