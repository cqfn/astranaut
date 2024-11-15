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
 * Tests covering {@link Interface} class.
 * @since 1.0.0
 */
class InterfaceTest {
    @Test
    void simpleEmptyInterface() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Empty interface.",
                " */",
                "interface Test0 {",
                "}",
                ""
            )
        );
        final Interface iface = new Interface("Test0", "Empty interface");
        final boolean result = this.testCodegen(iface, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void publicEmptyInterface() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Public empty interface.",
                " */",
                "public interface Test1 {",
                "}",
                ""
            )
        );
        final Interface iface = new Interface("Test1", "Public empty interface");
        iface.makePublic();
        final boolean result = this.testCodegen(iface, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void interfaceInheritsAnother() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Interface inherits another.",
                " */",
                "interface Test2 extends Test3 {",
                "}",
                ""
            )
        );
        final Interface iface = new Interface("Test2", "Interface inherits another");
        iface.setExtendsList("Test3");
        final boolean result = this.testCodegen(iface, expected);
        Assertions.assertTrue(result);
    }

    @Test
    void interfaceInheritsOtherTwo() {
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * Interface inherits other two.",
                " */",
                "interface Test4 extends Test5, Test6 {",
                "}",
                ""
            )
        );
        final Interface iface = new Interface("Test4", "Interface inherits other two");
        iface.setExtendsList("Test5", "Test6");
        final boolean result = this.testCodegen(iface, expected);
        Assertions.assertTrue(result);
    }

    /**
     * Tests the source code generation from an object describing a class.
     * @param iface Object describing an interface
     * @param expected Expected generated code
     * @return Test result, {@code true} if the generated code matches the expected code
     */
    private boolean testCodegen(final Interface iface, final String expected) {
        boolean oops = false;
        boolean equals = false;
        try {
            final SourceCodeBuilder builder = new SourceCodeBuilder();
            iface.build(0, builder);
            final String actual = builder.toString();
            equals = expected.equals(actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        return !oops && equals;
    }
}
