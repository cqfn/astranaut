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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.cqfn.astranaut.core.DraftNode;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.exceptions.VisualizerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link TreeVisualizer} class.
 *
 * @since 0.2
 */
public class TreeVisualizerTest {
    /**
     * Test for a single node visualization.
     * @param temp A temporary directory
     */
    @Test
    public void testSingleNodeVisualization(@TempDir final Path temp) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("TestNode");
        ctor.setData("value");
        final Node root = ctor.createNode();
        final TreeVisualizer visualizer = new TreeVisualizer(root);
        final Path img = temp.resolve("node.png");
        boolean oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final VisualizerException | IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for a null node visualization.
     * @param temp A temporary directory
     */
    @Test
    public void testNullNodeVisualization(@TempDir final Path temp) {
        final Node root = EmptyTree.INSTANCE;
        final TreeVisualizer visualizer = new TreeVisualizer(root);
        final Path img = temp.resolve("null.png");
        boolean oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final VisualizerException | IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for a node visualization with data encoding.
     * @param temp A temporary directory
     */
    @Test
    public void testNodeVisualizationWithEncoding(@TempDir final Path temp) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("DataNode");
        ctor.setData("<va\'l&u\"e>");
        final Node root = ctor.createNode();
        final TreeVisualizer visualizer = new TreeVisualizer(root);
        final Path img = temp.resolve("data.png");
        boolean oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final VisualizerException | IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for a tree visualization.
     * @param temp A temporary directory
     */
    @Test
    public void testTreeVisualization(@TempDir final Path temp) {
        final DraftNode.Constructor addition = new DraftNode.Constructor();
        addition.setName("Addition");
        final DraftNode.Constructor left = new DraftNode.Constructor();
        left.setName("IntegerLiteral");
        left.setData("2");
        final DraftNode.Constructor right = new DraftNode.Constructor();
        right.setName("DoubleLiteral");
        right.setData("3");
        addition.setChildrenList(Arrays.asList(left.createNode(), right.createNode()));
        final Node root = addition.createNode();
        final TreeVisualizer visualizer = new TreeVisualizer(root);
        final Path img = temp.resolve("tree.svg");
        boolean oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final VisualizerException | IOException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test for a wrong extension of a file to be generated.
     * @param temp A temporary directory
     */
    @Test
    public void testWrongExtension(@TempDir final Path temp) {
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("Exception");
        final Node root = ctor.createNode();
        final TreeVisualizer visualizer = new TreeVisualizer(root);
        final Path img = temp.resolve("node.jpg");
        boolean oops = false;
        try {
            visualizer.visualize(new File(img.toString()));
        } catch (final VisualizerException | IOException exception) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
