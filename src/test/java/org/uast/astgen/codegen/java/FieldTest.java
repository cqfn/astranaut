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
 * Tests for {@link Field} class.
 *
 * @since 1.0
 */
public class FieldTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing code generation with private field.
     */
    @Test
    public void privateField() {
        final Field field = new Field("The fragment", "Fragment", "fragment");
        final String expected = this.readTest("private_field.txt");
        final String actual = field.generate(1);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Testing code generation with public, static, final and initialized field.
     */
    @Test
    public void publicStaticFinalInitField() {
        final Field field = new Field("The type of the node", "Type", "TYPE");
        field.makePublic();
        field.makeStaticFinal();
        field.setInitExpr("new TypeImpl()");
        final String expected = this.readTest("public_static_final_field.txt");
        final String actual = field.generate(0);
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
            result = new FilesReader(FieldTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
