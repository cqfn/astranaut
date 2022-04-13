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
 * Tests for {@link Interface} class.
 *
 * @since 1.0
 */
public class InterfaceTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing code generation with interface descriptor.
     */
    @Test
    public void testInterface() {
        final Interface iface = new Interface("DSL rule", "Rule");
        final MethodDescriptor method = new MethodDescriptor(
            "Generates source code from the rule",
            "generate"
        );
        method.addArgument("Options", "opt", "The options set");
        iface.addMethod(method);
        final String expected = this.readTest("interface.txt");
        final String actual = iface.generate(0);
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
            result = new FilesReader(InterfaceTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
