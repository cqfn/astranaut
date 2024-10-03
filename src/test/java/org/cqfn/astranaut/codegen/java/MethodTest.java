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
 * Tests covering {@link Method} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class MethodTest {
    @Test
    void simpleMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Simple method.",
                " */",
                "String getData() {",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Simple method");
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void publicMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Public method.",
                " */",
                "public String getData() {",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Public method");
        method.makePublic();
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void protectedMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Protected method.",
                " */",
                "protected String getData() {",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Protected method");
        method.makeProtected();
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void privateMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Private method.",
                " */",
                "private String getData() {",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Private method");
        method.makePrivate();
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void staticMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Static method.",
                " */",
                "static String getData() {",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Static method");
        method.makeStatic();
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void finalMethod() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Final method.",
                " */",
                "final String getData() {",
                "}",
                ""
            )
        );
        final Method method = new Method("String", "getData", "Final method");
        method.makeFinal();
        final boolean result = this.testCodegen(method, expected);
        Assertions.assertTrue(result);
    }

    /**
     * Tests the source code generation from an object describing a method.
     * @param method Object describing a method
     * @param expected Expected generated code
     * @return Test result, {@code true} if the generated code matches the expected code
     */
    private boolean testCodegen(final Method method, final String expected) {
        boolean oops = false;
        boolean equals = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            method.build(0, builder);
            final String actual = builder.toString();
            equals = expected.equals(actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return !oops && equals;
    }
}
