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
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link PatternDescriptor} class.
 * @since 1.0.0
 */
class PatternDescriptorTest {
    @Test
    void matchNodeWithChildrenAndPatternWithout() {
        final PatternDescriptor pattern = new PatternDescriptor(
            "A",
            null,
            Collections.emptyList()
        );
        final Node node = DraftNode.create("A(B,C)");
        final boolean matches = pattern.matchNode(node, new Extracted());
        Assertions.assertFalse(matches);
    }

    @Test
    void matchNodeWithoutChildrenAndPatternWith() {
        final PatternDescriptor pattern = new PatternDescriptor(
            "A",
            null,
            Arrays.asList(
                new PatternDescriptor("B", null, Collections.emptyList()),
                new PatternDescriptor("C", null, Collections.emptyList())
            )
        );
        final Node node = DraftNode.create("A");
        final boolean matches = pattern.matchNode(node, new Extracted());
        Assertions.assertFalse(matches);
    }

    @Test
    void matchPatternWithHole() {
        final PatternDescriptor pattern = new PatternDescriptor(
            "C",
            null,
            Arrays.asList(
                UntypedHole.getInstance(1),
                UntypedHole.getInstance(2)
            )
        );
        final Extracted extracted = new Extracted();
        Node node = DraftNode.create("C(D,E)");
        boolean matches = pattern.matchNode(node, extracted);
        Assertions.assertTrue(matches);
        Assertions.assertFalse(extracted.getNodes(1).isEmpty());
        Assertions.assertEquals("D", extracted.getNodes(1).get(0).getTypeName());
        Assertions.assertFalse(extracted.getNodes(2).isEmpty());
        Assertions.assertEquals("E", extracted.getNodes(2).get(0).getTypeName());
        node = DraftNode.create("C(D)");
        matches = pattern.matchNode(node, extracted);
        Assertions.assertFalse(matches);
    }

    @Test
    void matchPatternWithRegularChildren() {
        final PatternDescriptor pattern = new PatternDescriptor(
            "F",
            null,
            Arrays.asList(
                new PatternDescriptor("G", null, Collections.emptyList()),
                new PatternDescriptor("H", null, Collections.emptyList())
            )
        );
        final Extracted extracted = new Extracted();
        Node node = DraftNode.create("F(G)");
        boolean matches = pattern.matchNode(node, extracted);
        Assertions.assertFalse(matches);
        node = DraftNode.create("F(G,I)");
        matches = pattern.matchNode(node, extracted);
        Assertions.assertFalse(matches);
        node = DraftNode.create("F(G,H)");
        matches = pattern.matchNode(node, extracted);
        Assertions.assertTrue(matches);
    }

    @Test
    void matchPatternWithOptionalChild() {
        final PatternDescriptor child = new PatternDescriptor(
            "O",
            null,
            Collections.emptyList()
        );
        child.setMatchingMode(PatternMatchingMode.OPTIONAL);
        final PatternDescriptor pattern = new PatternDescriptor(
            "K",
            null,
            Arrays.asList(
                new PatternDescriptor("L", null, Collections.emptyList()),
                child
            )
        );
        final Extracted extracted = new Extracted();
        Node node = DraftNode.create("K(L)");
        boolean matches = pattern.matchNode(node, extracted);
        Assertions.assertTrue(matches);
        node = DraftNode.create("K(L,O)");
        matches = pattern.matchNode(node, extracted);
        Assertions.assertTrue(matches);
        node = DraftNode.create("K(L,M)");
        matches = pattern.matchNode(node, extracted);
        Assertions.assertFalse(matches);
    }
}
