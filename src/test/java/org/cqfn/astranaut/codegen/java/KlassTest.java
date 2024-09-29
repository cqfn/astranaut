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
        final Klass klass = new Klass("Test0");
        final boolean result = this.testCodegen(klass, "class Test0 {\n}\n");
        Assertions.assertTrue(result);
    }

    @Test
    void simplePublicEmptyClass() {
        final Klass klass = new Klass("Test1");
        klass.makePublic();
        final boolean result = this.testCodegen(klass, "public class Test1 {\n}\n");
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
