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
package org.cqfn.astranaut.example;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.cqfn.astranaut.api.TreeProcessor;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.exceptions.WrongFileExtension;
import org.cqfn.astranaut.core.utils.TreeVisualizer;
import org.cqfn.astranaut.exceptions.ProcessorException;

/**
 * Sample class.
 * @since 0.2.1
 */
public class TreeProcessorVisualizer {
    /**
     * The main function.
     * @param args The command-line arguments
     * @throws ProcessorException If loading DSL rules fails
     * @throws WrongFileExtension If visualization fails
     * @throws IOException If an error during input or output actions occurs
     */
    public static void main(final String... args)
        throws IOException, WrongFileExtension, ProcessorException {
        final Node tree = createSampleTree();
        final TreeProcessor processor = new TreeProcessor();
        processor.loadRules("Data/rules.txt");
        final Node result = processor.transform(tree);
        final TreeVisualizer visualizer = new TreeVisualizer(result);
        visualizer.visualize(new File("Data/tree.png"));
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
