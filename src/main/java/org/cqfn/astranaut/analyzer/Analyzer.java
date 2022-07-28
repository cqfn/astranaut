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

package org.cqfn.astranaut.analyzer;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.cqfn.astranaut.exceptions.DuplicateRule;
import org.cqfn.astranaut.exceptions.ExtendedNodeNotFound;
import org.cqfn.astranaut.exceptions.GeneratorException;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Disjunction;
import org.cqfn.astranaut.rules.Empty;
import org.cqfn.astranaut.rules.Extension;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Statement;
import org.cqfn.astranaut.rules.Vertex;
import org.cqfn.astranaut.utils.Pair;

/**
 * Analyzes vertices hierarchy described with DSL.
 *
 * @since 0.1.5
 */
@SuppressWarnings("PMD.CloseResource")
public class Analyzer {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(Analyzer.class.getName());

    /**
     * Mappings between a vertex type and a result of its analysis.
     */
    private final Map<Vertex, Result> info;

    /**
     * Stack to save nodes with tagged types to be analyzed.
     */
    private final Stack<Node> stack;

    /**
     * Storage of nodes.
     */
    private final VertexStorage storage;

    /**
     * Constructor.
     * @param descriptors The vertex descriptors
     * @param language The programming language
     * @throws DuplicateRule exception if vertices described in rules
     *  contain duplications
     */
    public Analyzer(
        final List<Statement<Vertex>> descriptors,
        final String language) throws DuplicateRule {
        this.storage = new VertexStorage(descriptors, language);
        this.info = new TreeMap<>();
        this.stack = new Stack<>();
    }

    /**
     * The hierarchy of names of groups the vertex type belongs to.
     * @param type The type of the vertex
     * @return The list of type names, cannot be {@code null}
     */
    public List<String> getHierarchy(final String type) {
        List<String> hierarchy = Collections.emptyList();
        final Vertex vertex = this.storage.getVertexByType(type, true);
        if (vertex != null) {
            hierarchy = this.info.get(vertex).getHierarchy();
        }
        return hierarchy;
    }

    /**
     * The list of tagged names which the provided node has.
     * @param type The type of the node
     * @return The list of tagged names, cannot be {@code null}
     */
    public List<TaggedName> getTags(final String type) {
        List<TaggedName> tags = new LinkedList<>();
        final Vertex vertex = this.storage.getVertexByType(type, true);
        if (vertex != null) {
            tags = this.info.get(vertex).getTaggedNames();
        }
        return tags;
    }

    /**
     * The list of vertex types that should be added to an import block
     *  of the specified node.
     * @param type The type of the vertex
     * @return The list of type names, cannot be {@code null}
     */
    public Set<String> getImports(final String type) {
        return this.storage.getVerticesToBeImported(type);
    }

    /**
     * Conducts analysis:
     * - processes green vertices;
     * - processes language-specific vertices.
     * @return Itself
     * @throws GeneratorException exception if vertices described in rules
     *  contain duplications or the node was not found
     */
    public Analyzer analyze() throws GeneratorException {
        this.storage.collectAndCheck();
        final List<Vertex> green = this.storage.getGreenVertices();
        this.pipeline(green);
        final List<Vertex> specific = this.storage.getSpecificVertices();
        this.pipeline(specific);
        return this;
    }

    /**
     * Conducts analysis of the provided vertex set:
     * - processes final vertices;
     * - processes abstract nodes.
     * @param vertices The list of vertices to be analyzed
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    public void pipeline(final List<Vertex> vertices) throws ExtendedNodeNotFound {
        for (final Vertex vertex : vertices) {
            if (vertex.isFinal()) {
                this.processFinalVertex(vertex);
            }
        }
        for (final Vertex vertex : vertices) {
            if (vertex.isAbstract() && !this.info.containsKey(vertex)) {
                this.processAbstractNode((Node) vertex, new LinkedList<>());
            }
        }
    }

    /**
     * Returns the programming language for which the rule is applied.
     * @return The language name
     */
    public String getLanguage() {
        return this.storage.getLanguage();
    }

    /**
     * Processes an abstract node:
     * - creates result entity;
     * - updates hierarchy for descendant nodes;
     * - iterates over descriptors to process them firstly and find descendants with tags;
     * - excludes not described nodes from processing;
     * - processes nodes with common tags;
     * - saves the result.
     * @param node The abstract node
     * @param ancestors The list of ancestor vertex types
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    private void processAbstractNode(final Node node, final List<String> ancestors)
        throws ExtendedNodeNotFound {
        final List<String> hierarchy = new LinkedList<>();
        hierarchy.add(node.getType());
        final Result result = new Result(hierarchy);
        result.addAncestors(ancestors);
        final List<Descriptor> descriptors =
            ((Disjunction) node.getComposition().get(0)).getDescriptors();
        ((LinkedList<String>) ancestors).addFirst(node.getType());
        int empty = 0;
        for (final Descriptor descriptor : descriptors) {
            if (descriptor.equals(Extension.INSTANCE)) {
                final List<String> extended = this.getHierarchyOfExtendedNode(node);
                ancestors.addAll(extended);
                result.addAncestors(extended);
            } else if (!descriptor.equals(Empty.INSTANCE)) {
                this.processVertex(descriptor.getType(), ancestors);
                if (this.storage.getVertexByType(descriptor.getType(), false) == null) {
                    empty += 1;
                }
            }
        }
        ((LinkedList<String>) ancestors).removeFirst();
        if (!this.stack.empty()) {
            this.findCommonTags(descriptors.size() - empty, result);
            if (result.containsTags()) {
                this.stack.push(node);
            }
        }
        this.info.put(node, result);
    }

    /**
     * Returns the hierarchy of the abstract green node that was extended
     *  by a language-specific node.
     * @param node The abstract language-specific node
     * @return The list of ancestor vertex types
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    private List<String> getHierarchyOfExtendedNode(final Node node)
        throws ExtendedNodeNotFound {
        List<String> hierarchy = new LinkedList<>();
        final Optional<Vertex> optional = this.storage.getGreenVertices()
            .stream()
            .filter(item -> node.getType().equals(item.getType()))
            .findFirst();
        if (optional.isPresent() && optional.get().isAbstract()) {
            if (this.info.containsKey(optional.get())) {
                final Vertex ancestor = optional.get();
                final Result result = this.info.get(ancestor);
                hierarchy = result.getHierarchy();
            }
        } else {
            throw new ExtendedNodeNotFound(node.getType());
        }
        return hierarchy;
    }

    /**
     * Finds common tags of an abstract node and its descendants:
     * - iterates over nodes in stack to collect common tagged names;
     * - if processed nodes count equals descendants size, update
     *  {@code overridden} fields and add tags to the ancestor.
     * @param count The count of described descendant nodes
     * @param ancestor The result of ancestor analysis
     */
    private void findCommonTags(final int count, final Result ancestor) {
        final Set<Node> descendants = new HashSet<>();
        final Set<TaggedName> common = new HashSet<>();
        int idx = 0;
        while (!this.stack.empty() && idx < count) {
            idx += 1;
            final Node node = this.stack.pop();
            final Result result = this.info.get(node);
            final List<TaggedName> names = result.getTaggedNames();
            for (final TaggedName name : names) {
                if (idx == 1) {
                    common.add(name);
                    descendants.add(node);
                    continue;
                }
                if (!common.contains(name)) {
                    common.remove(name);
                    continue;
                }
                descendants.add(node);
            }
        }
        if (count == idx) {
            for (final Node descendant : descendants) {
                final Result result = this.info.get(descendant);
                result.setOverriddenTags(common);
            }
            for (final TaggedName name : common) {
                ancestor.addTaggedName(name.getTag(), name.getType());
            }
        }
    }

    /**
     * Processes the vertex:
     * - updates the already processed vertices results;
     * - pushes nodes with tagged names to the stack;
     * - finds a not processed node from the list and processes it if
     *  it is abstract;
     * - ignores a not described vertex, but prints warning about it.
     * @param type The node type
     * @param ancestors The list of ancestor node types
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    private void processVertex(final String type, final List<String> ancestors)
        throws ExtendedNodeNotFound {
        final Vertex vertex = this.storage.getVertexByType(type, false);
        if (vertex != null && this.info.containsKey(vertex)) {
            final Result result = this.info.get(vertex);
            result.addAncestors(ancestors);
            if (result.containsTags() && !this.stack.contains(vertex)) {
                this.stack.push((Node) vertex);
            }
        } else if (vertex != null && vertex.isAbstract()) {
            this.processAbstractNode((Node) vertex, ancestors);
        }
    }

    /**
     * Conducts initial processing of final vertices:
     * - creates the result entity;
     * - adds tagged names of children to result.
     * @param vertex The final vertex
     */
    private void processFinalVertex(final Vertex vertex) {
        final List<String> hierarchy = new LinkedList<>();
        hierarchy.add(vertex.getType());
        final Result result = new Result(hierarchy);
        if (vertex.isOrdinary()) {
            final Node node = (Node) vertex;
            final List<Child> composition = node.getComposition();
            for (final Child child : composition) {
                final Descriptor descriptor = (Descriptor) child;
                if (!descriptor.getTag().isEmpty()) {
                    result.addTaggedName(
                        descriptor.getTag(),
                        descriptor.getType()
                    );
                }
            }
        }
        this.info.put(vertex, result);
    }

    /**
     * Stores vertices to be analyzed and checks their validity.
     *
     * @since 0.1.5
     */
    private static class VertexStorage {
        /**
         * The vertex descriptors.
         */
        private final List<Statement<Vertex>> descriptors;

        /**
         * Vertices related to current programming language and green nodes.
         */
        private final List<Vertex> vertices;

        /**
         * Vertices related to green nodes.
         */
        private List<Vertex> green;

        /**
         * Vertices related to the specified programming language.
         */
        private List<Vertex> specific;

        /**
         * The programming language for which the analysis is conducted.
         */
        private final String language;

        /**
         * Constructor.
         * @param descriptors The vertex descriptors
         * @param language The programming language
         */
        VertexStorage(
            final List<Statement<Vertex>> descriptors,
            final String language) {
            this.descriptors = descriptors;
            this.language = language;
            this.vertices = new LinkedList<>();
            this.green = new LinkedList<>();
            this.specific = new LinkedList<>();
        }

        /**
         * Retrieves the list of vertices related to the selected language and green vertices,
         * checks for redundant vertices and sorts the list of vertices.
         * @throws DuplicateRule exception if vertices described in rules
         *  contain duplications
         */
        public void collectAndCheck() throws DuplicateRule {
            final List<Vertex> common = new LinkedList<>();
            final List<Vertex> target = new LinkedList<>();
            for (final Statement<Vertex> statement : this.descriptors) {
                if (statement.getLanguage().isEmpty()) {
                    common.add(statement.getRule());
                }
                if (this.language.equals(statement.getLanguage())
                    && !this.language.isEmpty()) {
                    target.add(statement.getRule());
                }
            }
            checkDuplicateVertices(common);
            checkDuplicateVertices(target);
            final List<Vertex> related = new LinkedList<>();
            related.addAll(common);
            related.addAll(target);
            final Set<String> types = new HashSet<>();
            for (final Vertex vertex : related) {
                if (vertex.isAbstract()) {
                    checkDuplicateInheritance(
                        ((Node) vertex).getComposition().get(0), types
                    );
                }
            }
            final VertexSorter sorter = new VertexSorter();
            this.green = Collections.unmodifiableList(sorter.sortVertices(common));
            this.specific = Collections.unmodifiableList(sorter.sortVertices(target));
            this.vertices.addAll(this.green);
            this.vertices.addAll(this.specific);
        }

        /**
         * Returns green vertices.
         * @return The list of green vertices
         */
        public List<Vertex> getGreenVertices() {
            return this.green;
        }

        /**
         * Returns language-specific vertices.
         * @return The list of vertices related to the specified
         *  programming language
         */
        public List<Vertex> getSpecificVertices() {
            return this.specific;
        }

        /**
         * Returns the programming language.
         * @return The name of the language
         */
        public String getLanguage() {
            return this.language;
        }

        /**
         * Returns types of vertices which should be imported into the generated class
         * of the specified node.
         * @param type The vertex type
         * @return The list of vertex types
         */
        public Set<String> getVerticesToBeImported(final String type) {
            final Set<String> imports = new LinkedHashSet<>();
            final Optional<Vertex> optional =
                this.specific.stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
            if (optional.isPresent() && optional.get().isOrdinary()) {
                final Node node = (Node) optional.get();
                final List<Child> children = node.getComposition();
                for (final Child child : children) {
                    final Descriptor descriptor = (Descriptor) child;
                    final String name = descriptor.getType();
                    if (!this.isInSpecificVertices(name) && this.isInGreenVertices(name)) {
                        imports.add(name);
                    }
                }
            }
            return imports;
        }

        /**
         * Gets a vertex from the list of green or the list of language-specific vertices.
         * @param type The vertex type
         * @param dedicated Indicates if that the requested node is from the language-specific list
         * @return The vertex
         */
        public Vertex getVertexByType(final String type, final boolean dedicated) {
            final Optional<Vertex> optional;
            if (dedicated && !this.language.isEmpty()) {
                optional = this.specific.stream()
                    .filter(item -> type.equals(item.getType()))
                    .findFirst();
            } else {
                optional = this.vertices.stream()
                    .filter(item -> type.equals(item.getType()))
                    .findFirst();
            }
            if (!optional.isPresent()) {
                final StringBuilder builder = new StringBuilder(70);
                builder
                    .append("The vertex ")
                    .append(type)
                    .append(" was not described in DSL rules. It will be ignored during analysis!");
                LOG.info(builder.toString());
            }
            return optional.orElse(null);
        }

        /**
         * Checks if the specified vertex is in the list of
         * language-specific vertices.
         * @param type The vertex type
         * @return Checking result
         */
        private boolean isInSpecificVertices(final String type) {
            return this.specific.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }

        /**
         * Checks if the specified vertex is in the list of
         * green vertices.
         * @param type The vertex type
         * @return Checking result
         */
        private boolean isInGreenVertices(final String type) {
            return this.green.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }

        /**
         * Checks of provided list of vertices contains duplicates.
         * @param related The list of related vertices
         * @throws DuplicateRule exception if vertices described in rules
         *  contain duplications
         */
        private static void checkDuplicateVertices(
            final List<Vertex> related) throws DuplicateRule {
            final List<Vertex> duplicates =
                related.stream()
                    .collect(Collectors.groupingBy(p -> p.getType(), Collectors.toList()))
                    .values()
                    .stream()
                    .filter(i -> i.size() > 1)
                    .flatMap(j -> j.stream())
                    .collect(Collectors.toList());
            final Set<String> types = new LinkedHashSet<>();
            for (final Vertex duplicate : duplicates) {
                types.add(duplicate.getType());
            }
            for (final String type : types) {
                throw new DuplicateRule(
                    new StringBuilder()
                        .append(type)
                        .append(" description appears several times (should once)")
                        .toString()
                );
            }
        }

        /**
         * Checks if child vertices in the right rule part of abstract nodes
         * inherit only one ancestor.
         * @param child The child vertex
         * @param types The list of already processed child types
         * @throws DuplicateRule exception if nodes described in rules
         *  contain duplications
         */
        private static void checkDuplicateInheritance(
            final Child child,
            final Set<String> types) throws DuplicateRule {
            final List<Descriptor> descriptors = ((Disjunction) child).getDescriptors();
            for (final Descriptor descriptor : descriptors) {
                if (types.contains(descriptor.getType())) {
                    throw new DuplicateRule(
                        new StringBuilder()
                            .append(descriptor)
                            .append(" inherits an abstract node several times (should once)")
                            .toString()
                    );
                }
                types.add(descriptor.getType());
            }
        }
    }

    /**
     * Contains methods for sorting vertices by descending order of their depth in the AST.
     *
     * @since 0.1.5
     */
    private static class VertexSorter {
        /**
         * Sorts the list of vertices by descending order of their depth.
         * @param list The initial list of vertices
         * @return The sorted list
         */
        private List<Vertex> sortVertices(final List<Vertex> list) {
            final Map<Vertex, Integer> depth = new TreeMap<>();
            final Map<Vertex, Boolean> processed = new TreeMap<>();
            for (final Vertex vertex : list) {
                depth.put(vertex, 1);
                if (vertex.isFinal()) {
                    processed.put(vertex, true);
                } else {
                    processed.put(vertex, false);
                }
            }
            for (final Vertex vertex : list) {
                if (vertex.isAbstract() && !processed.get(vertex)) {
                    final List<Vertex> descendants = VertexSorter.getDescendantVerticesByType(
                        (Node) vertex,
                        list
                    );
                    if (VertexSorter.descendantsFinal(descendants)) {
                        depth.put(vertex, 2);
                        processed.put(vertex, true);
                    } else {
                        final Pair<Vertex, List<Vertex>> pair = new Pair<>(vertex, list);
                        this.getMaxDepth(
                            pair, depth, processed
                        );
                    }
                }
            }
            return VertexSorter.sortByValue(depth);
        }

        /**
         * Gets a maximum depth among descendants of a vertex.
         * @param pair The pair of a vertex and an initial list
         * @param depth The mappings of a vertex with its depth
         * @param processed The mappings of a vertex with the flag indicating
         *  if its final depth was found
         * @return The value of a vertex depth
         */
        private Integer getMaxDepth(
            final Pair<Vertex, List<Vertex>> pair,
            final Map<Vertex, Integer> depth,
            final Map<Vertex, Boolean> processed) {
            Integer max = 2;
            Integer value;
            final List<Vertex> descendants = VertexSorter.getDescendantVerticesByType(
                (Node) pair.getKey(),
                pair.getValue()
            );
            for (final Vertex vertex : descendants) {
                if (processed.get(vertex)) {
                    value = depth.get(vertex);
                } else {
                    value = this.getMaxDepth(
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
         * Gets a list of vertices which are descendants of the specified node.
         * @param node The node
         * @param vertices The list of all vertices
         * @return The list of vertices
         */
        private static List<Vertex> getDescendantVerticesByType(
            final Node node,
            final List<Vertex> vertices) {
            final Child child = node.getComposition().get(0);
            final List<Descriptor> descriptors = ((Disjunction) child).getDescriptors();
            final List<Vertex> result = new LinkedList<>();
            for (final Descriptor descriptor : descriptors) {
                final String type = descriptor.getType();
                final Optional<Vertex> optional =
                    vertices.stream()
                        .filter(item -> type.equals(item.getType()))
                        .findFirst();
                optional.ifPresent(result::add);
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
