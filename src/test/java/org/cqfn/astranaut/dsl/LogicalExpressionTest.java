/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link LogicalExpression} class.
 * @since 1.0.0
 */
class LogicalExpressionTest {
    @Test
    void testBaseInterface() {
        final LogicalExpression expression = new OrExpression(
            Arrays.asList(
                new PatternDescriptor("AAA", null, Collections.emptyList()),
                new PatternDescriptor("BBB", null, Collections.emptyList())
            )
        );
        Assertions.assertEquals("|(AAA, BBB)", expression.toString(true));
        Assertions.assertEquals("|(AAA, BBB)", expression.toString(false));
        expression.setNegationFlag();
        Assertions.assertEquals("~|(AAA, BBB)", expression.toString(true));
        Assertions.assertEquals("~|(AAA, BBB)", expression.toString(false));
        expression.setMatchingMode(PatternMatchingMode.OPTIONAL);
        Assertions.assertEquals("[~|(AAA, BBB)]", expression.toString(true));
        expression.setMatchingMode(PatternMatchingMode.REPEATED);
        Assertions.assertEquals("{~|(AAA, BBB)}", expression.toString(true));
    }

    @Test
    void createWithEmptyList() {
        boolean oops = false;
        try {
            new OrExpression(Collections.emptyList());
        } catch (final IllegalArgumentException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
