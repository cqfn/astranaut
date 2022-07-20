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

import org.cqfn.astranaut.base.EmptyTree;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link JsonDeserializer} class.
 *
 * @since 0.2
 */
public class JsonDeserializerTest {
    /**
     * The type IntegerLiteral.
     */
    private static final String INT_LITERAL = "IntegerLiteral";

    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/api/";

    /**
     * Test for a tree deserialization from a JSON file.
     */
    @Test
    public void testDeserialization() {
        Node result = EmptyTree.INSTANCE;
        final JsonDeserializer deserializer =
            new JsonDeserializer(JsonDeserializerTest.TESTS_PATH.concat("test_3_source.json"));
        boolean oops = false;
        try {
            result = deserializer.deserialize();
        } catch (final ProcessorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertEquals("Addition", result.getTypeName());
        Assertions.assertEquals("", result.getData());
        Assertions.assertEquals(2, result.getChildCount());
        final Node first = result.getChild(0);
        Assertions.assertEquals(JsonDeserializerTest.INT_LITERAL, first.getTypeName());
        Assertions.assertEquals("2", first.getData());
        Assertions.assertEquals(0, first.getChildCount());
        final Node second = result.getChild(1);
        Assertions.assertEquals(JsonDeserializerTest.INT_LITERAL, second.getTypeName());
        Assertions.assertEquals("3", second.getData());
        Assertions.assertEquals(0, second.getChildCount());
    }

    /**
     * Test for exception while reading a JSON file.
     */
    @Test
    public void testFilesReaderException() {
        final String filename = JsonDeserializerTest.TESTS_PATH.concat("test_3.json");
        final JsonDeserializer deserialize =
            new JsonDeserializer(filename);
        boolean oops = false;
        String msg = "";
        try {
            deserialize.deserialize();
        } catch (final BaseException exception) {
            oops = true;
            msg = exception.getErrorMessage();
        }
        Assertions.assertTrue(oops);
        Assertions.assertEquals(
            "Could not read the file that contains source tree: ".concat(filename),
            msg
        );
    }
}
