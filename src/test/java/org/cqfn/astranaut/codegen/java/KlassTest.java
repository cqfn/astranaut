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
 * Tests covering {@link Klass} class.
 * @since 1.0.0
 */
class KlassTest {
    @Test
    void simpleEmptyClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Empty class.",
                " */",
                "class Test0 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test0", "Empty class");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void simplePublicEmptyClass() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Public empty class.",
                " */",
                "public class Test1 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test1", "Public empty class");
        klass.makePublic();
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void withVersionNumber() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class with version number.",
                " * @since 1.0.0",
                " */",
                "class Test2 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test2", "Class with version number");
        klass.setVersion("1.0.0");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void implementsTwoInterfaces() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Class implementing interfaces.",
                " */",
                "class Test3 implements Test4, Test5 {",
                "}",
                ""
            )
        );
        final Klass klass = new Klass("Test3", "Class implementing interfaces");
        klass.setImplementsList("Test4", "Test5");
        final boolean result = this.testCodegen(klass, expected);
        Assertions.assertTrue(result);
    }

    /**
     * Tests the source code generation from an object describing a class.
     * @param klass Object describing a class
     * @param expected Expected generated code
     * @return Test result, {@code true} if the generated code matches the expected code
     */
    private boolean testCodegen(final Klass klass, final String expected) {
        boolean oops = false;
        boolean equals = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            klass.build(0, builder);
            final String actual = builder.toString();
            equals = expected.equals(actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return !oops && equals;
    }
}
