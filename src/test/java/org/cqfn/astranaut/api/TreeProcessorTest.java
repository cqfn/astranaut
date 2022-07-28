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
package org.cqfn.astranaut.api;

import java.util.Arrays;
import org.cqfn.astranaut.base.DraftNode;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TreeProcessor} class.
 *
 * @since 0.2
 */
public class TreeProcessorTest {
    /**
     * The type IntegerLiteral.
     */
    private static final String INT_LITERAL = "IntegerLiteral";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/api/";

    /**
     * Test for a tree transformation.
     */
    @Test
    public void testTreeTransformation() {
        final Node tree = this.createSampleTree();
        final TreeProcessor processor = new TreeProcessor();
        boolean oops = false;
        Node result = null;
        try {
            processor.loadRules(TreeProcessorTest.TESTS_PATH.concat("/test_0_rules.txt"));
            result = processor.transform(tree);
        } catch (final BaseException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(TreeProcessorTest.INT_LITERAL, result.getTypeName());
        Assertions.assertEquals("5", result.getData());
        Assertions.assertEquals(0, result.getChildCount());
    }

    /**
     * Test for a tree transformation with rules from a string.
     */
    @Test
    public void testTreeTransformationFromString() {
        final Node tree = this.createSampleTree();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(
            "Addition(IntegerLiteral<\"2\">, IntegerLiteral<\"3\">) -> IntegerLiteral<\"5\"> ;"
        );
        final Node result = processor.transform(tree);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(TreeProcessorTest.INT_LITERAL, result.getTypeName());
        Assertions.assertEquals("5", result.getData());
        Assertions.assertEquals(0, result.getChildCount());
    }

    /**
     * Test for exception while reading a DSL file.
     */
    @Test
    public void testFilesReaderException() {
        final TreeProcessor processor = new TreeProcessor();
        boolean oops = false;
        String msg = "";
        final String filename = TreeProcessorTest.TESTS_PATH.concat("test_1_rules.txt");
        try {
            processor.loadRules(filename);
        } catch (final BaseException exception) {
            oops = true;
            msg = exception.getErrorMessage();
        }
        Assertions.assertTrue(oops);
        Assertions.assertEquals(
            "Could not read DSL file: ".concat(filename),
            msg
        );
    }

    /**
     * Test for exception while reading a DSL file containing bugs.
     */
    @Test
    public void testIncorrectDslFile() {
        final TreeProcessor processor = new TreeProcessor();
        boolean oops = false;
        final String filename = TreeProcessorTest.TESTS_PATH.concat("test_0_bug_rules.txt");
        boolean result = true;
        try {
            result = processor.loadRules(filename);
        } catch (final ProcessorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertFalse(result);
    }

    /**
     * Create a simple tree for testing.
     * @return Tree
     */
    public Node createSampleTree() {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName("Addition");
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName(TreeProcessorTest.INT_LITERAL);
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName(TreeProcessorTest.INT_LITERAL);
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        return addition.createNode();
    }
}
