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
 * Tests covering {@link SourceCodeBuilder} class.
 * @since 1.0.0
 */
class SourceCodeBuilderTest {
    @Test
    void addDelimitedLine() {
        final SourceCodeBuilder code = new SourceCodeBuilder();
        boolean oops = false;
        try {
            code.add(2, "abcd\nefg");
            final String actual = code.toString();
            final String expected = "        abcd\n        efg\n";
            Assertions.assertEquals(expected, actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void addingALineThatIsTooLong() {
        final SourceCodeBuilder code = new SourceCodeBuilder();
        boolean oops = false;
        final int length = SourceCodeBuilder.MAX_LINE_LENGTH + 1;
        try {
            code.add(0, new String(new char[length]).replace('\0', '#'));
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Codegen", exception.getInitiator());
            Assertions.assertEquals(
                "The line of code is too long: '###################...'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }
}
