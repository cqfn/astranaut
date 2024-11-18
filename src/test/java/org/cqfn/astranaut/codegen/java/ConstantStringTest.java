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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ConstantStrings} class.
 * @since 1.0.0
 */
class ConstantStringTest {
    @Test
    void stringsWithoutPrefix() {
        final Klass klass = new Klass("Test", "Test class");
        final ConstantStrings strings = new ConstantStrings(klass, "", "The '#' string");
        Assertions.assertEquals(
            "Test.EXPRESSION",
            strings.createStaticField("Expression")
        );
        Assertions.assertEquals(
            "Test.VRBL_DCLRTN",
            strings.createStaticField("VariableDeclaration")
        );
        Assertions.assertEquals(
            "Test.HUGE_VARIABLE_N",
            strings.createStaticField("HugeVariableNameThatShouldBeTruncated")
        );
    }

    @Test
    void stringsWithPrefix() {
        final Klass klass = new Klass("Test", "Another test class");
        final ConstantStrings strings = new ConstantStrings(
            klass,
            "TYPE",
            "The '#' string"
        );
        Assertions.assertEquals(
            "Test.TYPE_EXPRESSION",
            strings.createStaticField("Expression")
        );
        Assertions.assertEquals(
            "Test.TYPE_VRBL_DCLRTN",
            strings.createStaticField("VariableDeclaration")
        );
        Assertions.assertEquals(
            "Test.TYPE_HUGE_VARIAB",
            strings.createStaticField("HugeVariableNameThatShouldBeTruncated")
        );
        Assertions.assertEquals(
            "Test.TYPE_AAAAAAAAAAA",
            strings.createStaticField("AaaaaaaaaaaaZzzzzzzzzzzz")
        );
    }

    @Test
    void badArguments() {
        boolean oops = false;
        try {
            new ConstantStrings(null, "", "");
        } catch (final IllegalArgumentException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
