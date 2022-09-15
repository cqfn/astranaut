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
package org.cqfn.astranaut.example;

import java.util.Arrays;
import org.cqfn.astranaut.api.JsonDeserializer;
import org.cqfn.astranaut.api.JsonSerializer;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.ProcessorCouldNotWriteFile;
import org.cqfn.astranaut.exceptions.ProcessorException;

/**
 * Sample class.
 * @since 0.2.1
 */
public class TreeSerializerDeserializer {
    /**
     * The main function.
     * @param args The command-line arguments
     * @throws BaseException If fails
     */
    public static void main(final String... args) throws BaseException {
        final Node tree = createSampleTree();
        serialize(tree);
        final Node result = deserialize();
        System.out.println(new JsonSerializer(result).serializeToJsonString());
    }

    /**
     * Serializes the tree to a JSON object and saves to a file.
     * @param tree The tree to be converted
     * @throws ProcessorCouldNotWriteFile If fails
     */
    private static void serialize(final Node tree)
            throws ProcessorCouldNotWriteFile {
        final JsonSerializer serializer = new JsonSerializer(tree);
        serializer.serializeToJsonFile("Data/tree.json");
    }

    /**
     * Converts the source string that contains a JSON object to a tree.
     * @return The result tree
     * @throws ProcessorException If fails
     */
    private static Node deserialize() throws ProcessorException {
        String source =  new FilesReader("Data/tree.json").readAsString(
            (FilesReader.CustomExceptionCreator<ProcessorException>) ()
                    -> new ProcessorException() {
                private static final long serialVersionUID = -6082231998626187934L;

                @Override
                public String getErrorMessage() {
                    return String.format(
                        "Could not read the file that contains source tree: %s",
                        "Data/tree.json"
                    );
                }
            }
        );
        final JsonDeserializer deserializer = new JsonDeserializer(source);
        return deserializer.deserialize();
    }

    /**
     * Creates a simple tree as an example.
     * @return The tree
     */
    public static Node createSampleTree() {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName("Addition");
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName("IntegerLiteral");
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName("IntegerLiteral");
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        return addition.createNode();
    }
}
