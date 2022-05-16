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

import java.io.IOException;
import org.cqfn.astgen.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CompilationUnit} class.
 *
 * @since 1.0
 */
public class CompilationUnitTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing code generation with compilation unit.
     */
    @Test
    public void testCompilationUnit() {
        final License license = new License("LICENSE.txt");
        final Interface iface = new Interface("DSL rule", "Rule");
        final MethodDescriptor method = new MethodDescriptor(
            "Generates source code from the rule",
            "generate"
        );
        method.addArgument(
            "Map<String, String>",
            "opt",
            "The options set"
        );
        iface.addMethod(method);
        final CompilationUnit unit = new CompilationUnit(
            license,
            "org.cqfn.astgen.codegen.java",
            iface
        );
        unit.addImport("java.util.Map");
        unit.addImport("java.util.Collections");
        final String expected = this.readTest("compilation_unit.txt");
        final String actual = unit.generate();
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
            result = new FilesReader(CompilationUnitTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
