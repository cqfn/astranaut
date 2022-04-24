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
 * Tests for {@link AdapterGenerator} class.
 *
 * @since 1.0
 */
public class AdapterGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing source code generation for rules that describe nodes.
     */
    @Test
    public void testNodeGeneration() {
        final Environment env = new TestEnvironment();
        final AdapterGenerator generator = new AdapterGenerator(env, "js", 10);
        final String actual = generator.generate().generate();
        final String expected = this.readTest("adapter_generator.txt");
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
            result = new FilesReader(AdapterGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
