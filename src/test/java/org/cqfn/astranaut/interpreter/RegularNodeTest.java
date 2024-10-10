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
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyFragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.ChildDescriptorExt;
import org.cqfn.astranaut.dsl.LiteralDescriptor;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link RegularNode} and {@link RegularBuilder} classes.
 * @since 1.0.0
 */
class RegularNodeTest {
    /**
     * The 'Expression' type.
     */
    private static final String EXPRESSION_TYPE = "Expression";

    /**
     * The 'Identifier' type.
     */
    private static final String IDENTIFIER_TYPE = "Identifier";

    @Test
    void nodeWithoutChildren() {
        final String name = "This";
        final RegularNodeDescriptor descriptor = new RegularNodeDescriptor(
            name,
            Collections.emptyList()
        );
        final Builder builder = descriptor.createBuilder();
        Assertions.assertTrue(builder.isValid());
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.setData("test"));
        Assertions.assertTrue(builder.setChildrenList(Collections.emptyList()));
        Assertions.assertFalse(
            builder.setChildrenList(Collections.singletonList(DummyNode.INSTANCE))
        );
        final Node node = builder.createNode();
        Assertions.assertSame(descriptor, node.getType());
        Assertions.assertEquals(name, node.getTypeName());
        Assertions.assertEquals("", node.getData());
        Assertions.assertEquals(0, node.getChildCount());
    }

    @Test
    void nodeWithOneObligatoryChild() {
        final RegularNodeDescriptor stmt = new RegularNodeDescriptor(
            "StatementExpression",
            Collections.singletonList(
                new ChildDescriptorExt(false, "", RegularNodeTest.EXPRESSION_TYPE)
            )
        );
        final Builder builder = stmt.createBuilder();
        Assertions.assertFalse(builder.isValid());
        Assertions.assertThrows(IllegalStateException.class, builder::createNode);
        builder.setFragment(EmptyFragment.INSTANCE);
        Assertions.assertTrue(builder.setData(""));
        Assertions.assertFalse(builder.setData("abc"));
        Assertions.assertFalse(builder.setChildrenList(Collections.emptyList()));
        Assertions.assertFalse(builder.isValid());
        Assertions.assertFalse(
            builder.setChildrenList(Collections.singletonList(DummyNode.INSTANCE))
        );
        Assertions.assertFalse(builder.isValid());
        Assertions.assertTrue(
            builder.setChildrenList(
                Collections.singletonList(RegularNodeTest.createBooleanExpression())
            )
        );
        Assertions.assertTrue(builder.isValid());
        final Node node = builder.createNode();
        Assertions.assertEquals(1, node.getChildCount());
        Assertions.assertEquals("StatementExpression(True)", node.toString());
    }

    @Test
    void nodeWithThreeChildren() {
        final RegularNodeDescriptor stmt = new RegularNodeDescriptor(
            "VariableDeclaration",
            Arrays.asList(
                new ChildDescriptorExt(true, "type", RegularNodeTest.IDENTIFIER_TYPE),
                new ChildDescriptorExt(false, "name", RegularNodeTest.IDENTIFIER_TYPE),
                new ChildDescriptorExt(true, "init", RegularNodeTest.EXPRESSION_TYPE)
            )
        );
        final Builder builder = stmt.createBuilder();
        Assertions.assertFalse(builder.isValid());
        Assertions.assertFalse(builder.setChildrenList(Collections.emptyList()));
        final Node type = RegularNodeTest.createIdentifier("boolean");
        final Node name = RegularNodeTest.createIdentifier("value");
        final Node init = RegularNodeTest.createBooleanExpression();
        Assertions.assertFalse(builder.setChildrenList(Collections.singletonList(init)));
        Assertions.assertTrue(builder.setChildrenList(Collections.singletonList(name)));
        final Node first = builder.createNode();
        Assertions.assertEquals(
            "VariableDeclaration(Identifier<\"value\">)",
            first.toString()
        );
        Assertions.assertTrue(builder.setChildrenList(Arrays.asList(name, init)));
        final Node second = builder.createNode();
        Assertions.assertEquals(
            "VariableDeclaration(Identifier<\"value\">, True)",
            second.toString()
        );
        Assertions.assertTrue(builder.setChildrenList(Arrays.asList(init, name)));
        final Node third = builder.createNode();
        Assertions.assertEquals(third.toString(), second.toString());
        Assertions.assertTrue(builder.setChildrenList(Arrays.asList(type, name, init)));
        final Node fourth = builder.createNode();
        Assertions.assertEquals(
            "VariableDeclaration(Identifier<\"boolean\">, Identifier<\"value\">, True)",
            fourth.toString()
        );
    }

    /**
     * Creates a node that is an "expression" in node hierarchy.
     * @return A node
     */
    private static Node createBooleanExpression() {
        final RegularNodeDescriptor constant = new RegularNodeDescriptor(
            "True",
            Collections.emptyList()
        );
        final AbstractNodeDescriptor boolexpr = new AbstractNodeDescriptor(
            "BooleanExpression",
            Collections.singletonList(constant.getName())
        );
        final AbstractNodeDescriptor expression = new AbstractNodeDescriptor(
            RegularNodeTest.EXPRESSION_TYPE,
            Collections.singletonList(boolexpr.getName())
        );
        boolean oops = false;
        try {
            boolexpr.addBaseDescriptor(expression);
            constant.addBaseDescriptor(boolexpr);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return constant.createBuilder().createNode();
    }

    /**
     * Creates a node that is an "identifier" in node hierarchy.
     * @param value Value of the identifier
     * @return A node
     */
    private static Node createIdentifier(final String value) {
        final LiteralDescriptor.Constructor ctor = new LiteralDescriptor.Constructor(
            RegularNodeTest.IDENTIFIER_TYPE
        );
        ctor.setType("String");
        final Builder builder = ctor.createDescriptor().createBuilder();
        builder.setData(value);
        return builder.createNode();
    }
}
