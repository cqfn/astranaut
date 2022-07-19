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

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.cqfn.astranaut.base.EmptyTree;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.base.Type;
import org.cqfn.astranaut.exceptions.VisualizerException;
import org.cqfn.astranaut.exceptions.WrongFileExtension;

/**
 * Visualizer of trees.
 *
 * @since 0.2
 */
public class TreeVisualizer {
    /**
     * The tree to be visualized.
     */
    private final Node tree;

    /**
     * Constructor.
     *
     * @param tree The tree to be visualized
     */
    public TreeVisualizer(final Node tree) {
        this.tree = tree;
    }

    /**
     * Renders a DOT text of the tree, renders an image from it and
     * saves it to the specified file.
     *
     * @param file A file of the tree visualization
     * @throws IOException If an error during input or output actions occurs
     * @throws VisualizerException If an error during visualization occurs
     */
    public void visualize(final File file) throws IOException, VisualizerException {
        final DotRender render = new DotRender(this.tree);
        final String dot = render.render();
        final ImageRender image = new ImageRender(dot);
        image.render(file);
    }

    /**
     * Renders a tree to an image using the external Graphviz tool.
     *
     * @since 0.2
     */
    private static class ImageRender {
        /**
         * DOT text with tree description.
         */
        private final String dot;

        /**
         * Constructor.
         *
         * @param dot The DOT file text
         */
        ImageRender(final String dot) {
            this.dot = dot;
        }

        /**
         * Renders data in a graphical format.
         *
         * @param file A file of the tree visualization
         * @throws VisualizerException If an error during visualization occurs
         * @throws IOException If an error during input or output actions occurs
         */
        public void render(final File file) throws VisualizerException, IOException {
            final Enum<Format> format = ImageRender.getFileExtension(file.getPath());
            final MutableGraph graph = new Parser().read(this.dot);
            Graphviz.useEngine(new GraphvizV8Engine());
            Graphviz.fromGraph(graph).render((Format) format).toFile(file);
        }

        /**
         * Get supported graphical file extension.
         *
         * @param path A path to the file to be rendered
         * @return A file extension
         * @throws VisualizerException If a file extension is invalid
         */
        private static Enum<Format> getFileExtension(final String path) throws VisualizerException {
            final Optional<String> optional = Optional.ofNullable(path)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(path.lastIndexOf('.') + 1));
            Enum<Format> format = null;
            if (optional.isPresent()) {
                final String ext = optional.get();
                if ("png".equals(ext)) {
                    format = Format.PNG;
                }
                if ("svg".equals(ext)) {
                    format = Format.SVG;
                }
            }
            if (format == null) {
                throw WrongFileExtension.INSTANCE;
            }
            return format;
        }
    }

    /**
     * Renders a tree to a DOT file.
     *
     * @since 0.2
     */
    private static class DotRender {
        /**
         * Node name start text.
         */
        private static final String NODE = "  node_";

        /**
         * Stores the generated DOT text.
         */
        @SuppressWarnings("PMD.AvoidStringBufferField")
        private final StringBuilder builder;

        /**
         * The tree to be converted.
         */
        private final Node tree;

        /**
         * Last index used for a node.
         */
        private int index;

        /**
         * Constructor.
         *
         * @param tree The tree to be converted
         */
        DotRender(final Node tree) {
            this.builder = new StringBuilder();
            this.tree = Objects.requireNonNull(tree);
            this.index = 0;
        }

        /**
         * Renders data to a DOT format.
         *
         * @return A rendered DOT text as string
         */
        public String render() {
            this.appendStart();
            this.processNode(this.tree);
            this.appendEnd();
            return this.builder.toString();
        }

        /**
         * Processes a node with all its children.
         *
         * @param node A node.
         */
        private void processNode(final Node node) {
            final boolean empty = node == null || node == EmptyTree.INSTANCE;
            if (empty) {
                this.appendNullNode();
            } else {
                final Type type = node.getType();
                this.appendNode(type.getName(), node.getData(), type.getProperty("color"));
                final int parent = this.index;
                for (int idx = 0; idx < node.getChildCount(); idx += 1) {
                    this.index += 1;
                    final int child = this.index;
                    this.processNode(node.getChild(idx));
                    this.appendEdge(parent, child, idx);
                }
            }
        }

        /**
         * Appends tree start text.
         */
        private void appendStart() {
            this.builder
                .append("digraph Tree {\n")
                .append("  node [shape=box style=rounded];\n");
        }

        /**
         * Appends tree end text.
         */
        private void appendEnd() {
            this.builder.append("}\n");
        }

        /**
         * Appends tree node text.
         *
         * @param type A node type
         * @param data A node data
         * @param color A node color
         */
        private void appendNode(final String type, final String data, final String color) {
            this.builder.append(DotRender.NODE).append(this.index).append(" [");
            this.builder.append("label=<").append(type);
            if (!data.isEmpty()) {
                this.builder.append("<br/><font color=\"blue\">");
                this.builder.append(encodeHtml(data));
                this.builder.append("</font>");
            }
            this.builder.append('>');
            if (!color.isEmpty()) {
                this.builder.append(" color=").append(color);
            }
            this.builder.append("];\n");
        }

        /**
         * Appends null node text.
         */
        private void appendNullNode() {
            this.builder
                .append(DotRender.NODE)
                .append(this.index)
                .append(" [label=<NULL>];\n");
        }

        /**
         * Appends tree edge text.
         *
         * @param parent A parent node index
         * @param child A child node index
         * @param label An edge label
         */
        private void appendEdge(final int parent, final int child, final Integer label) {
            this.builder
                .append(DotRender.NODE)
                .append(parent)
                .append(" -> ")
                .append("node_")
                .append(child);
            if (label != null) {
                this.builder
                    .append(" [label=\" ")
                    .append(label)
                    .append("\"]");
            }
            this.builder.append(";\n");
        }

        /**
         * Encodes text into an HTML-compatible format replacing characters,
         * which are not accepted in HTML, with corresponding HTML escape symbols.
         *
         * @param str Text to be encoded in HTML
         * @return An encoded text
         */
        private static String encodeHtml(final String str) {
            final StringBuilder result = new StringBuilder();
            final int len = str.length();
            for (int idx = 0; idx < len; idx += 1) {
                final char symbol = str.charAt(idx);
                switch (symbol) {
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    case '\'':
                        result.append("&apos;");
                        break;
                    case '\"':
                        result.append("&quot;");
                        break;
                    case '&':
                        result.append("&amp;");
                        break;
                    default:
                        result.append(symbol);
                        break;
                }
            }
            return result.toString();
        }
    }
}
