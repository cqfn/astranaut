/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import java.util.Arrays;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link CompilationUnit} class.
 * @since 1.0.0
 */
class CompilationUnitTest {
    @Test
    void withoutImports() {
        final License license = new License("Copyright (c) 2024 John Doe");
        final Package pkg = new Package("org.cqfn.astranaut.test0");
        final Klass klass = new Klass("Test0", "This class does nothing.");
        klass.setVersion("0.0.1");
        final CompilationUnit unit = new CompilationUnit(license, pkg, klass);
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/*",
                " * Copyright (c) 2024 John Doe",
                " */",
                "package org.cqfn.astranaut.test0;",
                "",
                "/**",
                " * This class does nothing.",
                " * @since 0.0.1",
                " */",
                "class Test0 {",
                "}",
                ""
            )
        );
        boolean oops = false;
        String actual = "";
        try {
            actual = unit.generateJavaCode();
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void withImports() {
        final License license = new License("Copyright (c) 2024 Jane Doe");
        final Package pkg = new Package("org.cqfn.astranaut.test1");
        final Klass klass = new Klass("Test1", "This class needs imports.");
        klass.setVersion("0.0.2");
        final CompilationUnit unit = new CompilationUnit(license, pkg, klass);
        Assertions.assertThrows(IllegalArgumentException.class, () -> unit.addImport("  "));
        unit.addImport("org.cqfn.astranaut.core.base.Node");
        unit.addImport("java.util.Arrays");
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/*",
                " * Copyright (c) 2024 Jane Doe",
                " */",
                "package org.cqfn.astranaut.test1;",
                "",
                "import java.util.Arrays;",
                "import org.cqfn.astranaut.core.base.Node;",
                "",
                "/**",
                " * This class needs imports.",
                " * @since 0.0.2",
                " */",
                "class Test1 {",
                "}",
                ""
                )
        );
        boolean oops = false;
        String actual = "";
        try {
            actual = unit.generateJavaCode();
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(expected, actual);
    }
}
