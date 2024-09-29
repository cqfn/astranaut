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
package org.cqfn.astranaut.interpreter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.ListNodeDescriptor;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ListNode} and {@link ListBuilder} classes.
 * @since 1.0.0
 */
class ListNodeTest {
    @Test
    void testBaseInterface() {
        final RegularNodeDescriptor empty = new RegularNodeDescriptor(
            "Empty",
            Collections.emptyList()
        );
        final AbstractNodeDescriptor stmt = new AbstractNodeDescriptor(
            "Statement",
            Collections.singletonList(empty.getName())
        );
        boolean oops = false;
        try {
            empty.addBaseDescriptor(stmt);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final Node child = empty.createBuilder().createNode();
        final ListNodeDescriptor descriptor = new ListNodeDescriptor(
            "StatementBlock",
            stmt.getName()
        );
        final Builder builder = descriptor.createBuilder();
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.setData("abc"));
        Assertions.assertTrue(builder.setChildrenList(Collections.emptyList()));
        Assertions.assertFalse(
            builder.setChildrenList(Collections.singletonList(DummyNode.INSTANCE))
        );
        Assertions.assertTrue(
            builder.setChildrenList(Collections.singletonList(child))
        );
        Assertions.assertTrue(
            builder.setChildrenList(Arrays.asList(child, child, child))
        );
        Assertions.assertTrue(builder.isValid());
        final Node node = builder.createNode();
        Assertions.assertEquals(
            "StatementBlock(Empty, Empty, Empty)",
            node.toString()
        );
        final List<ChildDescriptor> children = node.getType().getChildTypes();
        Assertions.assertEquals(1, children.size());
        Assertions.assertEquals(stmt.getName(), children.get(0).getType());
    }
}
