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
package org.cqfn.astranaut.codegen.java;

import java.util.Collections;
import org.cqfn.astranaut.dsl.PatternDescriptor;
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link TransformationGenerator} class.
 * @since 1.0.0
 */
class TransformationGeneratorTest {
    @Test
    void testBaseInterface() {
        final TransformationGenerator generator = new TransformationGenerator(
            new TransformationDescriptor(
                Collections.singletonList(
                    new PatternDescriptor("AAA", null, Collections.emptyList())
                ),
                new ResultingSubtreeDescriptor("BBB", null, Collections.emptyList())
            )
        );
        final Rule rule = generator.getRule();
        Assertions.assertEquals("AAA -> BBB", rule.toString());
    }
}
