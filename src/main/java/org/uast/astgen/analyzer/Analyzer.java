/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.uast.astgen.exceptions.DuplicateRule;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Disjunction;
import org.uast.astgen.rules.Extension;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.rules.Vertex;

/**
 * Analyzes nodes hierarchy described with DSL.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public class Analyzer {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(Analyzer.class.getName());

    /**
     * Mappings between a node type and a result of its analysis.
     */
    private final Map<String, Result> info;

    /**
     * Stack to save nodes with tagged types to be analyzed.
     */
    private final Stack<String> stack;

    /**
     * Storage of nodes.
     */
    private final NodeStorage storage;

    /**
     * Constructor.
     * @param descriptors The node descriptors
     * @param language The programming language
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    public Analyzer(
        final List<Statement<Vertex>> descriptors,
        final String language) throws DuplicateRule {
        this.storage = new NodeStorage(descriptors, language);
        this.info = new HashMap<>();
        this.stack = new Stack<>();
    }

    /**
     * The hierarchy of names of groups the node type belongs to.
     * @param type The type of the node
     * @return The list of type names, cannot be {@code null}
     */
    public List<String> getHierarchy(final String type) {
        List<String> hierarchy = new LinkedList<>();
        if (this.info.containsKey(type)) {
            hierarchy = this.info.get(type).getHierarchy();
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
        if (this.info.containsKey(type)) {
            tags = this.info.get(type).getTaggedNames();
        }
        return tags;
    }

    /**
     * The list of node types that should be added to an import block
     *  of the specified node.
     * @param type The type of the node
     * @return The list of type names, cannot be {@code null}
     */
    public Set<String> getImports(final String type) {
        return this.storage.getVerticesToBeImported(type);
    }

    /**
     * Conducts analysis of the provided node set:
     * - processes ordinary nodes (with children);
     * - processes abstract nodes.
     * @return Itself
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    public Analyzer analyze() throws DuplicateRule {
        this.storage.collectAndCheck();
        final List<Vertex> vertices = this.storage.getNodes();
        for (final Vertex vertex : vertices) {
            if (vertex.isOrdinary()) {
                this.processOrdinaryNode((Node) vertex);
            }
        }
        for (final Vertex vertex : vertices) {
            if (vertex.isAbstract() && !this.info.containsKey(vertex.getType())) {
                this.processAbstractNode((Node) vertex, new LinkedList<>());
            }
        }
        return this;
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
     * @param ancestors The list of ancestor node types
     */
    private void processAbstractNode(final Node node, final List<String> ancestors) {
        final List<String> hierarchy = new LinkedList<>();
        hierarchy.add(node.getType());
        final Result result = new Result(hierarchy);
        result.addAncestors(ancestors);
        final Child child = node.getComposition().get(0);
        final List<Descriptor> descriptors = ((Disjunction) child).getDescriptors();
        ((LinkedList<String>) ancestors).addFirst(node.getType());
        int empty = 0;
        for (final Descriptor descriptor : descriptors) {
            if (descriptor instanceof Extension) {
                result.addAncestors(Collections.singletonList(node.getType()));
            } else {
                final String type = descriptor.getType();
                this.processVertex(type, ancestors);
                if (this.info.get(type).equals(NullResult.INSTANCE)) {
                    empty += 1;
                }
            }
        }
        ((LinkedList<String>) ancestors).removeFirst();
        if (!this.stack.empty()) {
            this.findCommonTags(descriptors.size() - empty, result);
            if (result.containsTags()) {
                this.stack.push(node.getType());
            }
        }
        this.info.put(node.getType(), result);
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
        final Set<String> descendants = new HashSet<>();
        final Set<TaggedName> common = new HashSet<>();
        int idx = 0;
        while (!this.stack.empty() && idx < count) {
            idx += 1;
            final String node = this.stack.pop();
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
            for (final String descendant : descendants) {
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
     * - updates the already processed nodes results;
     * - pushes nodes with tagged names to the stack;
     * - finds a not processed node from the list and processes it if
     *  it is abstract;
     * - ignores a not described node, but prints warning about it.
     *  {@code overridden} fields and add tags to the ancestor
     * @param type The node type
     * @param ancestors The list of ancestor node types
     */
    private void processVertex(final String type, final List<String> ancestors) {
        if (this.info.containsKey(type)) {
            final Result result = this.info.get(type);
            result.addAncestors(ancestors);
            if (result.containsTags() && !this.stack.contains(type)) {
                this.stack.push(type);
            }
        } else {
            final Optional<Vertex> optional =
                this.storage.getNodes().stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
            if (optional.isPresent() && optional.get().isAbstract()) {
                this.processAbstractNode((Node) optional.get(), ancestors);
            } else {
                this.info.put(type, NullResult.INSTANCE);
                final StringBuilder builder = new StringBuilder(70);
                builder
                    .append("The vertex ")
                    .append(type)
                    .append(" was not described in DSL rules. It will be ignored during analysis!");
                LOG.info(builder.toString());
            }
        }
    }

    /**
     * Conducts initial processing of ordinary nodes:
     * - creates the result entity;
     * - adds tagged names of children to result.
     * @param node The ordinary node
     */
    private void processOrdinaryNode(final Node node) {
        final List<String> hierarchy = new LinkedList<>();
        hierarchy.add(node.getType());
        final Result result = new Result(hierarchy);
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
        this.info.put(node.getType(), result);
    }

    /**
     * Stores nodes to be analyzed and checks their validity.
     *
     * @since 1.0
     */
    private static class NodeStorage {
        /**
         * The node descriptors.
         */
        private final List<Statement<Vertex>> descriptors;

        /**
         * Nodes related to current programming language and green nodes.
         */
        private List<Vertex> nodes;

        /**
         * Nodes related to green nodes.
         */
        private final List<Vertex> green;

        /**
         * Nodes related to the specified programming language.
         */
        private final List<Vertex> specific;

        /**
         * The programming language for which the analysis is conducted.
         */
        private final String language;

        /**
         * Constructor.
         * @param descriptors The node descriptors
         * @param language The programming language
         */
        NodeStorage(
            final List<Statement<Vertex>> descriptors,
            final String language) {
            this.descriptors = descriptors;
            this.language = language;
            this.nodes = new LinkedList<>();
            this.green = new LinkedList<>();
            this.specific = new LinkedList<>();
        }

        /**
         * Retrieves the list of nodes related to the selected language and green nodes,
         * checks for redundant nodes and sorts the list of nodes.
         * @throws DuplicateRule exception if nodes described in rules
         *  contain duplications
         */
        public void collectAndCheck() throws DuplicateRule {
            final List<Vertex> common = new LinkedList<>();
            final List<Vertex> target = new LinkedList<>();
            for (final Statement<Vertex> statement : this.descriptors) {
                if ("".equals(statement.getLanguage())) {
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
            this.green.addAll(common);
            this.specific.addAll(target);
            this.nodes = Collections.unmodifiableList(this.sortVertices(related));
        }

        /**
         * Returns all nodes.
         * @return The list of all nodes
         */
        public List<Vertex> getNodes() {
            return this.nodes;
        }

        /**
         * Returns green nodes.
         * @return The list of green nodes
         */
        public List<Vertex> getGreenVertices() {
            return this.green;
        }

        /**
         * Returns language-specific nodes.
         * @return The list of nodes related to the specified
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
         * Returns types of nodes which should be imported into the generated class
         * of the specified node.
         * @param type The node type
         * @return The list of node types
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
         * Checks if the specified node is in the list of
         * language-specific nodes.
         * @param type The node type
         * @return Checking result
         */
        private boolean isInSpecificVertices(final String type) {
            return this.specific.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }

        /**
         * Checks if the specified node is in the list of
         * green nodes.
         * @param type The node type
         * @return Checking result
         */
        private boolean isInGreenVertices(final String type) {
            return this.green.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }

        /**
         * Checks of provided list of nodes contains duplicates.
         * @param related The list of related nodes
         * @throws DuplicateRule exception if nodes described in rules
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
         * Checks if child nodes in the right rule part of abstract nodes
         * inherit only one ancestor.
         * @param child The child node
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

        /**
         * Sorts the list of vertex by descending order of their depth.
         * @param vertices The initial list of vertices
         * @return The sorted list
         */
        private List<Vertex> sortVertices(final List<Vertex> vertices) {
            final Map<Vertex, Integer> depth = new HashMap<>();
            final Map<Vertex, Boolean> processed = new HashMap<>();
            for (final Vertex vertex : vertices) {
                depth.put(vertex, 1);
                if (vertex.isTerminal()) {
                    processed.put(vertex, true);
                } else {
                    processed.put(vertex, false);
                }
            }
            for (final Vertex vertex : vertices) {
                if (vertex.isAbstract() && !processed.get(vertex)) {
                    final List<Vertex> descendants = this.getDescendantVerticesByType(
                        (Node) vertex,
                        vertices
                    );
                    if (this.descendantsTerminal(descendants)) {
                        depth.put(vertex, 2);
                        processed.put(vertex, true);
                    } else {
                        final Pair<Vertex, List<Vertex>> pair = new Pair<>(vertex, vertices);
                        this.getMaxDepth(
                            pair, depth, processed
                        );
                    }
                }
            }
            return this.sortByValue(depth);
        }

        /**
         * Gets a maximum depth among descendants of a node.
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
            final List<Vertex> descendants = this.getDescendantVerticesByType(
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
         * Checks if all the specified descendants are terminal vertices
         * (ordinary nodes, lists or literals).
         * @param vertices The list of vertices
         * @return Checking result
         */
        private static boolean descendantsTerminal(final List<Vertex> vertices) {
            boolean result = true;
            for (final Vertex vertex : vertices) {
                if (!vertex.isTerminal()) {
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
