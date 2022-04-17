/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.rules.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analyzes nodes hierarchy described with DSL.
 *
 * @since 1.0
 */
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
    private final HashMap<String, Result> info;

    /**
     * Constructor.
     * @param descriptors The node descriptors
     * @param language The programming language
     */
    public Analyzer(final List<Statement<Node>> descriptors, final String language) throws ExpectedOnlyOneEntity {
        this.nodes = Collections.unmodifiableList(getNodes(descriptors, language));
        this.language = language;
        this.info = new HashMap<>();
    }

    private List<Node> getNodes(
            final List<Statement<Node>> descriptors,
            final String language) throws ExpectedOnlyOneEntity {
        final List<Statement<Node>> related = getRelatedDescriptors(descriptors, language);
        final List<Node> nodes = new ArrayList<>();
        for (final Statement<Node> descriptor : related) {
            final Node node = descriptor.getRule();
            nodes.add(node);
        }
        checkDuplicateNodes(nodes);
        return nodes;
    }

    /**
     * Retrieves the list of nodes related to the selected language
     * and green nodes.
     * @param descriptors The node descriptors
     * @param language The programming language
     */
    private List<Statement<Node>> getRelatedDescriptors(
        final List<Statement<Node>> descriptors,
        final String language) {
        return descriptors.stream()
            .filter(
                node -> language.equals(node.getLanguage()) ||
                "green".equals(node.getLanguage())
            )
            .collect(Collectors.toList());
    }

    /**
     * Sorts the list of nodes by their depth in inheritance.
     *
     * @param descriptors The node descriptors
     */
    private List<Node> sortNodes(
        final List<Statement<Node>> descriptors) {
        final HashMap<Node, Integer> depth = countDepth();
        for (final Statement<Node> descriptor : descriptors) {
            final Node parent = descriptor.getRule();
            final List<Child> composition = parent.getComposition();
            for (final Child child : composition) {
                if (child instanceof Disjunction) {
                    final List<Descriptor> desc = ((Disjunction) child).getDescriptors();
                }
            }
        }
        return new ArrayList<>();
    }

    private HashMap<Node, Integer> countDepth() {
        return new HashMap<Node, Integer>();
    }

    private static void checkDuplicateNodes(
            final List<Node> nodes
        ) throws ExpectedOnlyOneEntity {
        Set<Node> duplicates =
            nodes.stream()
            .filter(node ->
                Collections.frequency(nodes, node) > 1)
            .collect(Collectors.toSet());
        for (final Node node : duplicates) {
            throw new ExpectedOnlyOneEntity(
                new StringBuilder()
                    .append(node.getType())
                    .append(" <- ...")
                    .toString()
            );
        }
    }



    /**
     * The hierarchy of names of groups the node type belongs to.
     * @param type The type of the node
     * @return The list of type names, cannot be {@code null}
     */
    public List<String> getHierarchy(final String type) {
        List<String> hierarchy = new ArrayList<>();
        if (info.containsKey(type)) {
            hierarchy =  info.get(type).getHierarchy();
        }
        return hierarchy;
    }

    public static class Result {
        private final List<String> hierarchy;

        private final List<TaggedName> tags;

        public Result(final String name) {
            this.hierarchy = new LinkedList<>();
            this.hierarchy.add(name);
            this.tags = new LinkedList<>();
        }

        public void addParent(final String name) {
            hierarchy.add(name);
        }

        public List<String> getHierarchy() {
            return this.hierarchy;
        }

        public List<TaggedName> getTags() {
            return this.tags;
        }
    }

    private static class TaggedName {
        final String tag;
        final String type;
        boolean overridden;

        public TaggedName(final String tag, final String type) {
            this.tag = tag;
            this.type = type;
            this.overridden = false;
        }

        public void setOverridden() {
            this.overridden = true;
        }
    }
}
