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
 * Tests for {@link MethodBody} class.
 *
 * @since 0.1.5
 */
class MethodBodyTest {
    /**
     * Testing code generation with a single line.
     */
    @Test
    void singleLine() {
        final MethodBody body = new MethodBody();
        body.setCode(" return x;");
        final String code = body.generate(2);
        Assertions.assertEquals("        return x;\n", code);
    }

    /**
     * Testing code generation with two lines.
     */
    @Test
    void twoLines() {
        final MethodBody body = new MethodBody();
        body.setCode("int x = 2 + 3;\nreturn x;");
        final String code = body.generate(1);
        Assertions.assertEquals("    int x = 2 + 3;\n    return x;\n", code);
    }

    /**
     * Testing code generation with 'if' statement.
     */
    @Test
    void ifStatement() {
        final String expected = "if (flag) {\n    builder.append(',');\n}\n";
        final MethodBody body = new MethodBody();
        body.setCode("if (flag) { builder.append(','); }");
        final String first = body.generate(0);
        Assertions.assertEquals(expected, first);
        body.setCode(expected);
        final String second = body.generate(0);
        Assertions.assertEquals(expected, second);
    }
}
