/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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
package org.cqfn.astranaut.analyzer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Vertex;

/**
 * Contains methods for sorting vertices by descending order of their depth in the AST.
 *
 * @since 0.1.5
 */
public class VertexSorter {
    /**
     * The initial list of vertices to be sorted.
     */
    private final List<Vertex> vertices;

    /**
     * Constructor.
     * @param vertices The list of vertices
     */
    public VertexSorter(final List<Vertex> vertices) {
        this.vertices = vertices;
    }

    /**
     * Sorts the list of vertices by descending order of their depth.
     * @return The sorted list
     */
    public List<Vertex> sortVertices() {
        final Map<Vertex, Integer> depth = new TreeMap<>();
        final Map<Vertex, Boolean> processed = new TreeMap<>();
        for (final Vertex vertex : this.vertices) {
            depth.put(vertex, 1);
            if (vertex.isFinal()) {
                processed.put(vertex, true);
            } else {
                processed.put(vertex, false);
            }
        }
        for (final Vertex vertex : this.vertices) {
            if (vertex.isAbstract() && !processed.get(vertex)) {
                final List<Vertex> descendants = new VerticesProcessor(this.vertices)
                    .getDescendantVerticesByType((Node) vertex);
                if (Sorter.descendantsFinal(descendants)) {
                    depth.put(vertex, 2);
                    processed.put(vertex, true);
                } else {
                    final Pair<Vertex, List<Vertex>> pair = new Pair<>(vertex, this.vertices);
                    Sorter.getMaxDepth(
                        pair, depth, processed
                    );
                }
            }
        }
        return Sorter.sortByValue(depth);
    }

    /**
     * Contains logic for sorting vertices by descending order of their depth in a tree.
     *
     * @since 0.1.5
     */
    private static class Sorter {
        /**
         * Gets a maximum depth among descendants of a vertex.
         * @param pair The pair of a vertex and an initial list
         * @param depth The mappings of a vertex with its depth
         * @param processed The mappings of a vertex with the flag indicating
         *  if its final depth was found
         * @return The value of a vertex depth
         */
        private static Integer getMaxDepth(
            final Pair<Vertex, List<Vertex>> pair,
            final Map<Vertex, Integer> depth,
            final Map<Vertex, Boolean> processed) {
            Integer max = 2;
            Integer value;
            final List<Vertex> descendants = new VerticesProcessor(pair.getValue())
                .getDescendantVerticesByType((Node) pair.getKey());
            for (final Vertex vertex : descendants) {
                if (processed.get(vertex)) {
                    value = depth.get(vertex);
                } else {
                    value = Sorter.getMaxDepth(
                        new Pair<>(vertex, pair.getValue()),
                        depth,
                        processed
                    );
                }
                if (value > max) {
                    max = value;
                }
            }
            max += 1;
            depth.put(pair.getKey(), max);
            processed.put(pair.getKey(), true);
            return max;
        }

        /**
         * Checks if all the specified descendants are final vertices
         * (ordinary nodes, lists or literals).
         * @param vertices The list of vertices
         * @return Checking result
         */
        private static boolean descendantsFinal(final List<Vertex> vertices) {
            boolean result = true;
            for (final Vertex vertex : vertices) {
                if (!vertex.isFinal()) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        /**
         * Converts the specified map to the list of vertices
         * sorted by descending order of their depth.
         * @param depth The mappings of a vertex with its depth
         * @return The sorted list of vertices
         */
        private static List<Vertex> sortByValue(final Map<Vertex, Integer> depth) {
            final List<Map.Entry<Vertex, Integer>> list = new LinkedList<>(depth.entrySet());
            list.sort(Map.Entry.comparingByValue());
            final List<Vertex> sorted = new LinkedList<>();
            for (final Map.Entry<Vertex, Integer> item : list) {
                sorted.add(item.getKey());
            }
            Collections.reverse(sorted);
            return sorted;
        }
    }
}
