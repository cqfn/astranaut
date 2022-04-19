/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
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
     * Nodes related to current programming language and green nodes.
     */
    private final List<Node> nodes;

    /**
     * The programming language for which the analysis is conducted.
     */
    private final String language;

    /**
     * Mappings between a node type and a result of its analysis.
     */
    private final Map<String, Result> info;

    /**
     * Stack to save nodes with tagged types to be analyzed.
     */
    private final Stack<String> stack;

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
        this.language = language;
        this.nodes = Collections.unmodifiableList(this.getRelatedNodes(descriptors));
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
     * Conducts analysis of the provided node set.
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    public void analyze() throws DuplicateRule {
        this.initialProcess();
        for (final Node node : this.nodes) {
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
        return this.language;
    }

    /**
     * Retrieves the list of nodes related to the selected language and green nodes,
     * and checks for redundant nodes.
     * @param descriptors The node descriptors
     * @return The list of nodes
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    private List<Node> getRelatedNodes(
        final List<Statement<Node>> descriptors) throws DuplicateRule {
        final List<Statement<Node>> statements =
            descriptors.stream()
                .filter(
                    node -> this.language.equals(node.getLanguage())
                    || "".equals(node.getLanguage())
                )
                .collect(Collectors.toList());
        final List<Node> related = new LinkedList<>();
        for (final Statement<Node> descriptor : statements) {
            final Node node = descriptor.getRule();
            related.add(node);
        }
        checkDuplicateNodes(related);
        return related;
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
        ((LinkedList) ancestors).addFirst(node.getType());
        int empty = 0;
        for (final Descriptor descriptor : descriptors) {
            final String type = descriptor.getType();
            this.processNode(type, ancestors);
            if (this.info.get(type).equals(NullResult.INSTANCE)) {
                empty += 1;
            }
        }
        ((LinkedList) ancestors).removeFirst();
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
                this.nodes.stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
            if (optional.isPresent() && optional.get().isAbstract()) {
                this.processAbstractNode(optional.get(), ancestors);
            } else {
                this.info.put(type, NullResult.INSTANCE);
            }
        }
    }

    /**
     * Conducts initial processing of input list of nodes:
     * - checks that abstract node descendants inherit only once;
     * - processes ordinary nodes (with children).
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    private void initialProcess() throws DuplicateRule {
        final Set<String> types = new HashSet<>();
        for (final Node node : this.nodes) {
            if (node.isAbstract()) {
                checkDuplicateInheritance(node.getComposition().get(0), types);
            }
            if (node.isOrdinary()) {
                this.processOrdinaryNode(node);
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
     * The class to store the result of analysis.
     * @since 1.0
     */
    private static class Result {
        /**
         * The node hierarchy (its type and types of its ancestors)
         * sorted by increasing depth of inheritance.
         */
        private final List<String> hierarchy;

        /**
         * The list of tagged names.
         */
        private final List<TaggedName> tags;

        /**
         * Constructor.
         * @param hierarchy The hierarchy list
         */
        Result(final List<String> hierarchy) {
            this.hierarchy = hierarchy;
            this.tags = new LinkedList<>();
        }

        /**
         * Adds ancestor types to the hierarchy.
         * @param names The list of ancestor type names
         */
        public void addAncestors(final List<String> names) {
            this.hierarchy.addAll(names);
        }

        /**
         * Adds a tagged type name.
         * @param tag The tag
         * @param type The type
         */
        public void addTaggedName(final String tag, final String type) {
            this.tags.add(new TaggedName(tag, type));
        }

        /**
         * Returns the hierarchy of the node.
         * @return The list of types
         */
        public List<String> getHierarchy() {
            return this.hierarchy;
        }

        /**
         * Returns the hierarchy of the node.
         * @return The list of types
         */
        public List<TaggedName> getTaggedNames() {
            return this.tags;
        }

        /**
         * Checks if the node contains tagged names.
         * @return Checking result
         */
        public boolean containsTags() {
            return !this.tags.isEmpty();
        }

        /**
         * Sets the {@code true} value to the {@code overridden} property of
         * the tagged names.
         * @param common The common tagged names
         */
        public void setOverriddenTags(final Set<TaggedName> common) {
            for (final TaggedName ancestor : common) {
                final int idx = this.tags.indexOf(ancestor);
                final TaggedName local = this.tags.get(idx);
                local.makeOverridden();
            }
        }
    }

    /**
     * The null results that is added to nodes that are not described in rules.
     *
     * @since 1.0
     */
    private static final class NullResult extends Result {
        /**
         * The instance.
         */
        public static final Result INSTANCE = new NullResult();

        /**
         * Constructor.
         */
        NullResult() {
            super(new LinkedList<>());
        }
    }

    /**
     * The class to store tagged names.
     *
     * @since 1.0
     */
    private static class TaggedName {
        /**
         * The tag.
         */
        private final String tag;

        /**
         * The type.
         */
        private final String type;

        /**
         * The flag indicates that the tag is overridden.
         */
        private boolean overridden;

        /**
         * Constructor.
         * @param tag The tag
         * @param type The type
         */
        TaggedName(final String tag, final String type) {
            this.tag = tag;
            this.type = type;
            this.overridden = false;
        }

        /**
         * Makes the tagged name overridden.
         */
        public void makeOverridden() {
            this.overridden = true;
        }

        /**
         * Returns the tag.
         * @return The tag
         */
        public String getTag() {
            return this.tag;
        }

        /**
         * Returns the type.
         * @return The type
         */
        public String getType() {
            return this.type;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder
                .append('{')
                .append(this.tag)
                .append(", ")
                .append(this.type)
                .append(", ")
                .append(this.overridden)
                .append('}');
            return builder.toString();
        }

        @Override
        public boolean equals(final Object obj) {
            final TaggedName name;
            boolean equal = false;
            if (obj instanceof TaggedName) {
                name = (TaggedName) obj;
                if (this.type.equals(name.getType())
                    && this.tag.equals(name.getTag())) {
                    equal = true;
                }
            }
            return equal;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.tag + this.type);
        }
    }
}
