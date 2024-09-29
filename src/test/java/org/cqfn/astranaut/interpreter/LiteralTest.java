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

import java.util.Collections;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.dsl.LiteralDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link Literal} and {@link LiteralBuilder} classes.
 * @since 1.0.0
 */
class LiteralTest {
    @Test
    void testBaseInterface() {
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("Identifier");
        ctor.setType("String");
        final String initial = "abc";
        ctor.setInitial(initial);
        final LiteralDescriptor descriptor = ctor.createDescriptor();
        Assertions.assertEquals(initial, descriptor.getInitial());
        final Builder builder = descriptor.createBuilder();
        Assertions.assertTrue(builder.isValid());
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setChildrenList(Collections.emptyList()));
        Assertions.assertFalse(
            builder.setChildrenList(Collections.singletonList(DummyNode.INSTANCE))
        );
        final Node first = builder.createNode();
        Assertions.assertEquals("Identifier<\"abc\">", first.toString());
        builder.setData("def");
        final Node second = builder.createNode();
        Assertions.assertEquals("Identifier<\"def\">", second.toString());
        Assertions.assertTrue(second.getType().getChildTypes().isEmpty());
        Assertions.assertEquals(0, second.getChildCount());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> second.getChild(0));
    }
}
