/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.cqfn.astranaut.parser;

import java.util.List;
import org.cqfn.astranaut.exceptions.NodeNameCapitalLetter;
import org.cqfn.astranaut.exceptions.OnlyOneListDescriptor;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Disjunction;
import org.cqfn.astranaut.rules.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link NodeParser} class.
 *
 * @since 0.1.5
 */
@SuppressWarnings("PMD.TooManyMethods")
public class NodeParserTest {
    /**
     * Test case: node with one child.
     */
    @Test
    public void oneChild() {
        final boolean result = this.run("UnaryMinus <- Expression");
        Assertions.assertTrue(result);
    }

    /**
     * Test case: node with two children.
     */
    @Test
    public void twoChildren() {
        final boolean result = this.run("Assignment <- LeftExpression, Expression");
        Assertions.assertTrue(result);
    }

    /**
     * Test case: node with two children with tags.
     */
    @Test
    public void twoChildrenWithTags() {
        final boolean result = this.run("Addition <- left@Epression, right@Expression");
        Assertions.assertTrue(result);
    }

    /**
     * Test case: node name started with low case.
     */
    @Test
    public void lowCaseError() {
        final boolean result = this.run(
            "unaryMinus <- expression",
            NodeNameCapitalLetter.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: node with optional child.
     */
    @Test
    public void optionalChild() {
        final boolean result = this.run(
            "VariableDeclaration <- [type@Identifier], name@Identifier, [Expression]"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: list node.
     */
    @Test
    public void listNode() {
        final boolean result = this.run("StatementList <- {Statement}");
        Assertions.assertTrue(result);
    }

    /**
     * Test case: list node and other nodes.
     */
    @Test
    public void listNodeAndOthers() {
        final boolean result = this.run(
            "Something <- AAA, {BBB}",
            OnlyOneListDescriptor.class
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: abstract node.
     */
    @Test
    public void abstractNode() {
        final boolean result = this.run(
            "Expression <- Addition | Subtraction | Multiplication | Division"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: abstract node with extension.
     */
    @Test
    public void abstractNodeWithExtension() {
        final String source = "BinaryExpression <- & | Exponent";
        boolean oops = false;
        try {
            final Node node = new NodeParser(source).parse();
            final String text = node.toString();
            Assertions.assertEquals(source, text);
            final List<Child> composition = node.getComposition();
            Assertions.assertFalse(composition.isEmpty());
            Assertions.assertTrue(composition.get(0) instanceof Disjunction);
            final Disjunction disjunction = (Disjunction) composition.get(0);
            final Descriptor descriptor = disjunction.getDescriptors().get(0);
            Assertions.assertEquals(DescriptorAttribute.EXT, descriptor.getAttribute());
            Assertions.assertEquals(0, descriptor.getHoleNumber());
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test case: abstract node with several extensions.
     */
    @Test
    public void abstractNodeWithExtensionException() {
        final boolean result = this.run(
            "BinaryExpression <- & | Exponent | &"
        );
        Assertions.assertFalse(result);
    }

    /**
     * Test case: prototype of an abstract node.
     */
    @Test
    public void abstractNodePrototype() {
        final boolean result = this.run(
            "Statement <- Return | 0"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Test case: empty node (no children and no data).
     */
    @Test
    public void emptyNode() {
        final boolean result = this.run(
            "This <- 0"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Runs the node parser.
     * @param source Source string
     * @return Test result ({@code true} means no exceptions)
     */
    private boolean run(final String source) {
        boolean oops = false;
        try {
            final Node node = new NodeParser(source).parse();
            final String text = node.toString();
            Assertions.assertEquals(source, text);
        } catch (final ParserException ignored) {
            oops = true;
        }
        return !oops;
    }

    /**
     * Runs the node parser and expects an exception.
     * @param source Source string
     * @param type Error type
     * @param <T> Exception class
     * @return Test result ({@code true} means parser throw expected exception)
     */
    private <T> boolean run(final String source, final Class<T> type) {
        boolean oops = false;
        try {
            new NodeParser(source).parse();
        } catch (final ParserException error) {
            final boolean result = type.isInstance(error);
            Assertions.assertTrue(result);
            oops = true;
        }
        return oops;
    }
}
