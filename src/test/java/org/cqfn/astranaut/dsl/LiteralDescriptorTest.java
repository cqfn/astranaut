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
package org.cqfn.astranaut.dsl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link LiteralDescriptor} class.
 * @since 1.0.0
 */
class LiteralDescriptorTest {
    @Test
    void emptyDescriptor() {
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("Invalid");
        Assertions.assertFalse(ctor.isValid());
        Assertions.assertThrows(IllegalStateException.class, ctor::createDescriptor);
    }

    @Test
    void nameOnly() {
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("Identifier");
        ctor.setType("String");
        Assertions.assertTrue(ctor.isValid());
        Assertions.assertEquals("Identifier <- 'String', ?, ?, ?, ?", ctor.toString());
        final LiteralDescriptor descriptor = ctor.createDescriptor();
        Assertions.assertEquals("Identifier <- 'String'", descriptor.toString());
    }

    @Test
    void nameAndInit() {
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("StringLiteral");
        ctor.setType("String");
        ctor.setInitial("\"\"");
        Assertions.assertTrue(ctor.isValid());
        Assertions.assertEquals(
            "StringLiteral <- 'String', '\"\"', ?, ?, ?",
            ctor.toString()
        );
        final LiteralDescriptor descriptor = ctor.createDescriptor();
        Assertions.assertEquals(
            "StringLiteral <- 'String', '\"\"'",
            descriptor.toString()
        );
    }

    @Test
    void nameInitSerializerParser() {
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("IntegerLiteral");
        ctor.setType("int");
        ctor.setInitial("0");
        ctor.setSerializer("String.valueOf(#)");
        ctor.setParser("Integer.parseInt(#)");
        Assertions.assertTrue(ctor.isValid());
        Assertions.assertEquals(
            "IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(#)', ?",
            ctor.toString()
        );
        final LiteralDescriptor descriptor = ctor.createDescriptor();
        Assertions.assertEquals(
            "IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(#)'",
            descriptor.toString()
        );
    }

    @Test
    void fullDescriptor() {
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("RealNumberLiteral");
        ctor.setType("double");
        ctor.setInitial("0.0");
        ctor.setSerializer("String.valueOf(#)");
        ctor.setParser("Double.parseDouble(#)");
        ctor.setException("NumberFormatException");
        Assertions.assertTrue(ctor.isValid());
        final String expected =
            "RealNumberLiteral <- 'double', '0.0', 'String.valueOf(#)', 'Double.parseDouble(#)', 'NumberFormatException'";
        Assertions.assertEquals(expected, ctor.toString());
        final LiteralDescriptor descriptor = ctor.createDescriptor();
        Assertions.assertEquals(expected, descriptor.toString());
    }

    @Test
    void serializerWithoutInit() {
        final LiteralDescriptor.Constructor ctor = new LiteralDescriptor.Constructor("X");
        ctor.setType("a");
        ctor.setSerializer("b");
        Assertions.assertFalse(ctor.isValid());
    }

    @Test
    void serializerWithoutParser() {
        final LiteralDescriptor.Constructor ctor = new LiteralDescriptor.Constructor("X");
        ctor.setType("a");
        ctor.setInitial("b");
        ctor.setSerializer("c");
        Assertions.assertFalse(ctor.isValid());
    }

    @Test
    void parserWithoutSerializer() {
        final LiteralDescriptor.Constructor ctor = new LiteralDescriptor.Constructor("X");
        ctor.setType("a");
        ctor.setInitial("b");
        ctor.setParser("c");
        Assertions.assertFalse(ctor.isValid());
    }

    @Test
    void exceptionWithoutParser() {
        final LiteralDescriptor.Constructor ctor = new LiteralDescriptor.Constructor("X");
        ctor.setType("a");
        ctor.setInitial("b");
        ctor.setException("c");
        Assertions.assertFalse(ctor.isValid());
    }
}
