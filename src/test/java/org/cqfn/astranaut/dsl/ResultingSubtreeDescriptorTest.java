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

import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DefaultFactory;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;
import org.cqfn.astranaut.parser.StringToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ResultingSubtreeDescriptor} class.
 * @since 1.0.0
 */
class ResultingSubtreeDescriptorTest {
    @Test
    void createNodeWithStaticData() {
        final ResultingSubtreeDescriptor descriptor = new ResultingSubtreeDescriptor(
            "A",
            new StaticString(new StringToken('\"', "test")),
            Collections.emptyList()
        );
        final Node node = descriptor.createNode(
            new Extracted(),
            DefaultFactory.EMPTY,
            EmptyFragment.INSTANCE
        );
        Assertions.assertEquals("A<\"test\">", node.toString());
    }

    @Test
    void createNodeWithBadFactory() {
        final ResultingSubtreeDescriptor descriptor = new ResultingSubtreeDescriptor(
            "A",
            new StaticString(new StringToken('\"', "test")),
            Collections.emptyList()
        );
        for (int test = 0; test < 3; test = test + 1) {
            final int level = test;
            final Factory factory = new Factory() {
                @Override
                public Type getType(final String name) {
                    return null;
                }

                @Override
                public Builder createBuilder(final String name) {
                    return new BadBuilder(level);
                }
            };
            final Node node = descriptor.createNode(
                new Extracted(),
                factory,
                EmptyFragment.INSTANCE
            );
            Assertions.assertSame(DummyNode.INSTANCE, node);
        }
    }

    @Test
    void createNodeWithInnerNode() {
        final ResultingSubtreeDescriptor descriptor = new ResultingSubtreeDescriptor(
            "A",
            null,
            Collections.singletonList(
                new ResultingSubtreeDescriptor(
                    "B",
                    null,
                    Collections.emptyList()
                )
            )
        );
        final Node node = descriptor.createNode(
            new Extracted(),
            DefaultFactory.EMPTY,
            EmptyFragment.INSTANCE
        );
        Assertions.assertEquals("A(B)", node.toString());
    }

    /**
     * Builder, which intentionally returns errors, for testing purposes.
     * @since 1.0.0
     */
    private static final class BadBuilder implements Builder {
        /**
         * Error level.
         */
        private final int level;

        /**
         * Constructor.
         * @param level Error level
         */
        private BadBuilder(final int level) {
            this.level = level;
        }

        @Override
        public void setFragment(final Fragment fragment) {
            this.getClass();
        }

        @Override
        public boolean setData(final String data) {
            return this.level > 0;
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return this.level > 1;
        }

        @Override
        public boolean isValid() {
            return this.level > 2;
        }

        @Override
        public Node createNode() {
            return DummyNode.INSTANCE;
        }
    }
}
