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
 * Tests for {@link PackageInfo} class.
 *
 * @since 1.0
 */
public class PackageInfoTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing code generation with package info.
     */
    @Test
    public void packageInfo() {
        final License license = new License("LICENSE_header.txt");
        final PackageInfo info = new PackageInfo(
            license,
            "This package created for test purposes",
            "org.uast.example"
        );
        info.setVersion("1.1");
        final String expected = this.readTest("package_info.txt");
        final String actual = info.generate();
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
            result = new FilesReader(PackageInfoTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
