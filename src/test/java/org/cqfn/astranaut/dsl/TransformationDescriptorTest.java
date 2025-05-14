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
import java.util.List;
import java.util.Optional;
import org.cqfn.astranaut.core.algorithms.conversion.ConversionResult;
import org.cqfn.astranaut.core.base.DefaultFactory;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link TransformationDescriptor} class.
 * @since 1.0.0
 */
class TransformationDescriptorTest {
    @Test
    void testBaseInterface() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new TransformationDescriptor(
                Collections.emptyList(),
                UntypedHole.getInstance(0)
            )
        );
        final TransformationDescriptor transform = new TransformationDescriptor(
            Collections.singletonList(
                new PatternDescriptor("AAA", null, Collections.emptyList())
            ),
            new ResultingSubtreeDescriptor("BBB", null, Collections.emptyList())
        );
        Assertions.assertTrue(transform.getDependencies().isEmpty());
        final NodeDescriptor node = new RegularNodeDescriptor("BBB", Collections.emptyList());
        transform.addDependency(node);
        Assertions.assertEquals(1, transform.getDependencies().size());
        Assertions.assertNotNull(transform.createGenerator());
    }

    @Test
    void conversionInappropriateShortList() {
        final TransformationDescriptor descriptor = new TransformationDescriptor(
            Arrays.asList(
                new PatternDescriptor("A", null, Collections.emptyList()),
                new PatternDescriptor("B", null, Collections.emptyList()),
                new PatternDescriptor("C", null, Collections.emptyList())
            ),
            UntypedHole.getInstance(1)
        );
        final List<Node> bad = Collections.singletonList(
            DraftNode.create("D")
        );
        final Optional<ConversionResult> result = descriptor.convert(
            bad,
            0,
            DefaultFactory.EMPTY
        );
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void extractingChildAsHole() {
        final TransformationDescriptor descriptor = new TransformationDescriptor(
            Arrays.asList(
                new PatternDescriptor(
                    "A",
                    null,
                    Collections.singletonList(
                        UntypedHole.getInstance(1)
                    )
                )
            ),
            UntypedHole.getInstance(1)
        );
        final Node child = DraftNode.create("B");
        final List<Node> list = Collections.singletonList(
            DraftNode.create("A", "", child)
        );
        final Optional<ConversionResult> result = descriptor.convert(
            list,
            0,
            DefaultFactory.EMPTY
        );
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(child, result.get().getNode());
    }

    @Test
    void badFactory() {
        final TransformationDescriptor descriptor = new TransformationDescriptor(
            Collections.singletonList(
                new PatternDescriptor("A", null, Collections.emptyList())
            ),
            new ResultingSubtreeDescriptor("B", null, Collections.emptyList())
        );
        final List<Node> good = Collections.singletonList(
            DraftNode.create("A")
        );
        final Factory factory = name -> DummyNode.TYPE;
        final Optional<ConversionResult> result = descriptor.convert(
            good,
            0,
            factory
        );
        Assertions.assertFalse(result.isPresent());
    }
}
