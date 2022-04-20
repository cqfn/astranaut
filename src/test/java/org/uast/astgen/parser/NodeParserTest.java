/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.NodeNameCapitalLetter;
import org.uast.astgen.exceptions.OnlyOneListDescriptor;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Node;

/**
 * Test for {@link NodeParser} class.
 *
 * @since 1.0
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
        final boolean result = this.run(
            "BinaryExpression <- & | Exponent"
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
            Assertions.assertInstanceOf(type, error);
            oops = true;
        }
        return oops;
    }
}
