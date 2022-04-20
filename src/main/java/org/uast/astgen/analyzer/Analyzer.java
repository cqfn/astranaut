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
import org.uast.astgen.exceptions.DuplicateRule;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Disjunction;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Statement;

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
        final List<Statement<Node>> descriptors,
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
        return this.storage.getNodesToBeImported(type);
    }

    /**
     * Conducts analysis of the provided node set:
     * - processes ordinary nodes (with children);
     * - processes abstract nodes.
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    public void analyze() throws DuplicateRule {
        this.storage.collectAndCheck();
        final List<Node> nodes = this.storage.getNodes();
        for (final Node node : nodes) {
            if (node.isOrdinary()) {
                this.processOrdinaryNode(node);
            }
        }
        for (final Node node : nodes) {
            if (node.isAbstract() && !this.info.containsKey(node.getType())) {
                this.processAbstractNode(node, new LinkedList<>());
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
            final String type = descriptor.getType();
            this.processNode(type, ancestors);
            if (this.info.get(type).equals(NullResult.INSTANCE)) {
                empty += 1;
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
     * Processes the node:
     * - updates the already processed nodes results;
     * - pushes nodes with tagged names to the stack;
     * - finds a not processed node from the list and processes it if;
     *  it is abstract;
     * - ignores a not described node, but prints warning about it.
     *  {@code overridden} fields and add tags to the ancestor
     * @param type The node type
     * @param ancestors The list of ancestor node types
     */
    private void processNode(final String type, final List<String> ancestors) {
        if (this.info.containsKey(type)) {
            final Result result = this.info.get(type);
            result.addAncestors(ancestors);
            if (result.containsTags()) {
                this.stack.push(type);
            }
        } else {
            final Optional<Node> optional =
                this.storage.getNodes().stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
            if (optional.isPresent() && optional.get().isAbstract()) {
                this.processAbstractNode(optional.get(), ancestors);
            } else {
                this.info.put(type, NullResult.INSTANCE);
                final StringBuilder builder = new StringBuilder(70);
                builder
                    .append("The node ")
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
        private final List<Statement<Node>> descriptors;

        /**
         * Nodes related to current programming language and green nodes.
         */
        private final List<Node> nodes;

        /**
         * Nodes related to green nodes.
         */
        private final List<Node> green;

        /**
         * Nodes related to the specified programming language.
         */
        private final List<Node> specific;

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
            final List<Statement<Node>> descriptors,
            final String language) {
            this.descriptors = descriptors;
            this.language = language;
            this.nodes = new LinkedList<>();
            this.green = new LinkedList<>();
            this.specific = new LinkedList<>();
        }

        /**
         * Retrieves the list of nodes related to the selected language and green nodes,
         * and checks for redundant nodes.
         * @throws DuplicateRule exception if nodes described in rules
         *  contain duplications
         */
        public void collectAndCheck() throws DuplicateRule {
            final List<Node> common = new LinkedList<>();
            final List<Node> target = new LinkedList<>();
            for (final Statement<Node> statement : this.descriptors) {
                if ("".equals(statement.getLanguage())) {
                    common.add(statement.getRule());
                }
                if (this.language.equals(statement.getLanguage())
                    && !this.language.isEmpty()) {
                    target.add(statement.getRule());
                }
            }
            checkDuplicateNodes(common);
            checkDuplicateNodes(target);
            final List<Node> related = new LinkedList<>();
            related.addAll(common);
            related.addAll(target);
            final Set<String> types = new HashSet<>();
            for (final Node node : related) {
                if (node.isAbstract()) {
                    checkDuplicateInheritance(node.getComposition().get(0), types);
                }
            }
            this.green.addAll(common);
            this.specific.addAll(target);
            this.nodes.addAll(related);
        }

        /**
         * Returns all nodes.
         * @return The list of all nodes
         */
        public List<Node> getNodes() {
            return this.nodes;
        }

        /**
         * Returns green nodes.
         * @return The list of green nodes
         */
        public List<Node> getGreenNodes() {
            return this.green;
        }

        /**
         * Returns language-specific nodes.
         * @return The list of nodes related to the specified
         *  programming language
         */
        public List<Node> getSpecificNodes() {
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
        public Set<String> getNodesToBeImported(final String type) {
            final Set<String> imports = new LinkedHashSet<>();
            final Optional<Node> optional =
                this.specific.stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
            if (optional.isPresent() && optional.get().isOrdinary()) {
                final Node node = optional.get();
                final List<Child> children = node.getComposition();
                for (final Child child : children) {
                    final Descriptor descriptor = (Descriptor) child;
                    final String name = descriptor.getType();
                    if (!this.isInSpecificNodes(name) && this.isInGreenNodes(name)) {
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
        private boolean isInSpecificNodes(final String type) {
            return this.specific.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }

        /**
         * Checks if the specified node is in the list of
         * green nodes.
         * @param type The node type
         * @return Checking result
         */
        private boolean isInGreenNodes(final String type) {
            return this.green.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }

        /**
         * Checks of provided list of nodes contains duplicates.
         * @param related The list of related nodes
         * @throws DuplicateRule exception if nodes described in rules
         *  contain duplications
         */
        private static void checkDuplicateNodes(
            final List<Node> related) throws DuplicateRule {
            final Set<Node> duplicates =
                related.stream()
                    .filter(node -> Collections.frequency(related, node) > 1)
                    .collect(Collectors.toSet());
            for (final Node node : duplicates) {
                throw new DuplicateRule(
                    new StringBuilder()
                        .append(node.getType())
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
    }
}
