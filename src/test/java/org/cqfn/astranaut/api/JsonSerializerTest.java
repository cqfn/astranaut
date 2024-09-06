/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.EmptyTree;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.ProcessorCouldNotWriteFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link JsonSerializer} class.
 *
 * @since 0.2
 */
class JsonSerializerTest {
    /**
     * The type IntegerLiteral.
     */
    private static final String INT_LITERAL = "IntegerLiteral";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/api/";

    /**
     * Test for a tree serialization to a JSON string.
     */
    @Test
    void testSerializationToString() {
        final JsonSerializer serializer = new JsonSerializer(
            Tree.createDraft("TestNode<\"value\">")
        );
        final String result = serializer.serializeToJsonString().replace("\r", "");
        boolean oops = false;
        String expected = "";
        try {
            expected =
                new FilesReader(
                    JsonSerializerTest.TESTS_PATH.concat("test_1_expected.txt")
                ).readAsString();
        } catch (final IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for a tree serialization to a JSON file.
     * @param temp A temporary directory
     */
    @Test
    void testSerializationToFile(@TempDir final Path temp) {
        final JsonSerializer serializer = new JsonSerializer(this.createSampleTree());
        boolean oops = false;
        String expected = "";
        String result = "";
        final Path actual = temp.resolve("result.json");
        try {
            serializer.serializeToJsonFile(actual.toString());
            expected =
                new FilesReader(
                    JsonSerializerTest.TESTS_PATH.concat("test_2_expected.json")
                ).readAsString();
            result = new FilesReader(actual.toString()).readAsString();
        } catch (final ProcessorCouldNotWriteFile | IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals(expected, result);
    }

    /**
     * Test for exception while writing to a JSON file.
     */
    @Test
    void testFilesWriterException() {
        final JsonSerializer serializer = new JsonSerializer(EmptyTree.INSTANCE);
        boolean oops = false;
        try {
            serializer.serializeToJsonFile("/");
        } catch (final ProcessorCouldNotWriteFile exception) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    /**
     * Create a simple tree for testing.
     * @return Tree
     */
    private Tree createSampleTree() {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName("Addition");
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName(JsonSerializerTest.INT_LITERAL);
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName(JsonSerializerTest.INT_LITERAL);
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        return new Tree(addition.createNode());
    }
}
