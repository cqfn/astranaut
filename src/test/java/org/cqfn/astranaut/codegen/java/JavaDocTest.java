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
 * Tests covering {@link JavaDoc} class.
 * @since 1.0.0
 */
class JavaDocTest {
    @Test
    void shortBrief() {
        final JavaDoc entity = new JavaDoc("This class doesn't do anything.");
        final SourceCodeBuilder code = new SourceCodeBuilder();
        boolean oops = false;
        try {
            entity.build(0, code);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * This class doesn't do anything.",
                " */",
                ""
            )
        );
        Assertions.assertEquals(expected, code.toString());
    }

    @Test
    void longBriefWithoutDot() {
        final String brief =
            "This entity does nothing.    It was added specifically to demonstrate how the code generator handles large descriptions by splitting them into separate lines";
        final JavaDoc entity = new JavaDoc(brief);
        final SourceCodeBuilder code = new SourceCodeBuilder();
        boolean oops = false;
        try {
            entity.build(2, code);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "        /**",
                "         * This entity does nothing. It was added specifically to demonstrate how the code",
                "         *  generator handles large descriptions by splitting them into separate lines.",
                "         */",
                ""
            )
        );
        Assertions.assertEquals(expected, code.toString());
    }

    @Test
    void withVersionNumber() {
        final JavaDoc entity = new JavaDoc("This entity has a version number.");
        entity.setVersion("1.0.0");
        final SourceCodeBuilder code = new SourceCodeBuilder();
        boolean oops = false;
        try {
            entity.build(0, code);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final String expected = String.join(
            "\n",
            Arrays.asList(
                "/**",
                " * This entity has a version number.",
                " * @since 1.0.0",
                " */",
                ""
            )
        );
        Assertions.assertEquals(expected, code.toString());
    }
}
