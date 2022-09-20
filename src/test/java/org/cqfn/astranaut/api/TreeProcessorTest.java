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
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link TreeProcessor} class.
 *
 * @since 0.2
 */
@SuppressWarnings("PMD.TooManyMethods")
class TreeProcessorTest {
    /**
     * The type IntegerLiteral.
     */
    private static final String INT_LITERAL = "IntegerLiteral";

    /**
     * The type Addition.
     */
    private static final String ADDITION = "Addition";

    /**
     * The type Subtraction.
     */
    private static final String SUBTRACTION = "Subtraction";

    /**
     * The rule that replaces addition with subtraction.
     */
    private static final String RULE = "Addition(#1, #2) -> Subtraction(#1, #2);";

    /**
     * The number "3".
     */
    private static final int THREE = 3;

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/api/";

    /**
     * Test for a tree transformation.
     */
    @Test
    void testTreeTransformation() {
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
    void testTreeTransformationFromString() {
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
    void testFilesReaderException() {
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
    void testIncorrectDslFile() {
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
     * Test for calculation of variants of a rule application
     * choosing from a list with a single rule.
     */
    @Test
    void testCalculatingTransformationVariants() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(TreeProcessorTest.RULE);
        Assertions.assertEquals(1, processor.countRules());
        final int result = processor.calculateVariants(0, initial);
        Assertions.assertEquals(TreeProcessorTest.THREE, result);
    }

    /**
     * Test for calculation of variants of a second rule application
     * choosing from a list with two rules.
     */
    @Test
    void testCalculatingTransformationVariantsFromList() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(
            "SimpleName<#1> -> Identifier<#1>; Addition(#1, #2) -> Subtraction(#1, #2);"
        );
        Assertions.assertEquals(2, processor.countRules());
        final int result = processor.calculateVariants(1, initial);
        Assertions.assertEquals(TreeProcessorTest.THREE, result);
    }

    /**
     * Test for transformation with a first variant.
     */
    @Test
    void testPartialTransformationFirstVar() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(TreeProcessorTest.RULE);
        final Node result = processor.partialTransform(0, 0, initial);
        Assertions.assertNotNull(result);
        final Node third = result.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, third.getTypeName());
        final Node second = third.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, second.getTypeName());
        final Node first = second.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.SUBTRACTION, first.getTypeName());
    }

    /**
     * Test for transformation with a second variant.
     */
    @Test
    void testPartialTransformationSecVar() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(TreeProcessorTest.RULE);
        final Node result = processor.partialTransform(0, 1, initial);
        Assertions.assertNotNull(result);
        final Node third = result.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, third.getTypeName());
        final Node second = third.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.SUBTRACTION, second.getTypeName());
        final Node first = second.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, first.getTypeName());
    }

    /**
     * Test for transformations with second and third variants.
     */
    @Test
    void testPartialTransformationTwoVars() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(TreeProcessorTest.RULE);
        Node result = processor.partialTransform(0, 1, initial);
        result = processor.partialTransform(0, 1, result);
        Assertions.assertNotNull(result);
        final Node third = result.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.SUBTRACTION, third.getTypeName());
        final Node second = third.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.SUBTRACTION, second.getTypeName());
        final Node first = second.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, first.getTypeName());
    }

    /**
     * Test for transformation with a wrong rule index specified.
     */
    @Test
    void testWrongIndexOfRule() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(TreeProcessorTest.RULE);
        final Node result = processor.partialTransform(4, 0, initial);
        Assertions.assertEquals(EmptyTree.INSTANCE, result);
    }

    /**
     * Test for transformation with a wrong variant index specified.
     */
    @Test
    void testWrongIndexOfVariant() {
        final Node initial = this.loadSampleTreeFromJson();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRulesFromString(TreeProcessorTest.RULE);
        final Node result = processor.partialTransform(0, 3, initial);
        Assertions.assertNotNull(result);
        final Node third = result.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, third.getTypeName());
        final Node second = third.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, second.getTypeName());
        final Node first = second.getChild(0);
        Assertions.assertEquals(TreeProcessorTest.ADDITION, first.getTypeName());
    }

    /**
     * Create a simple tree for testing.
     * @return Tree
     */
    private Node createSampleTree() {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName(TreeProcessorTest.ADDITION);
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName(TreeProcessorTest.INT_LITERAL);
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName(TreeProcessorTest.INT_LITERAL);
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        return addition.createNode();
    }

    /**
     * Load a sample tree from JSON file for testing.
     * @return Deserialized tree
     */
    private Node loadSampleTreeFromJson() {
        final String file = TreeProcessorTest.TESTS_PATH.concat("test_calculation.json");
        boolean oops = false;
        String source = "";
        try {
            source = new FilesReader(file).readAsString(
                (FilesReader.CustomExceptionCreator<ProcessorException>) ()
                    -> new ProcessorException() {
                        private static final long serialVersionUID = -2486266117492218703L;

                        @Override
                        public String getErrorMessage() {
                            return String.format(
                                "Could not read the file that contains source tree: %s",
                                file
                            );
                        }
                    }
            );
        } catch (final ProcessorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final JsonDeserializer deserializer = new JsonDeserializer(source);
        return deserializer.deserialize();
    }
}
