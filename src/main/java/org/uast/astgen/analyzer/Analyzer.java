/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.rules.*;
import org.uast.astgen.scanner.Token;
import sun.security.krb5.internal.crypto.Des;

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
     * Stack to save nodes to be analyzed.
     */
    private final Stack<String> stack;

    /**
     * Constructor.
     * @param descriptors The node descriptors
     * @param language The programming language
     */
    public Analyzer(final List<Statement<Node>> descriptors, final String language) throws ExpectedOnlyOneEntity {
        this.nodes = Collections.unmodifiableList(getRelatedNodes(descriptors, language));
        this.language = language;
        this.info = new HashMap<>();
        this.stack = new Stack<>();
    }

    /**
     * Retrieves the list of nodes related to the selected language and green nodes
     * and checks for redundant nodes.
     * @param descriptors The node descriptors
     * @param language The programming language
     */
    private List<Node> getRelatedNodes(
            final List<Statement<Node>> descriptors,
            final String language) throws ExpectedOnlyOneEntity {
        final List<Statement<Node>> related =
            descriptors.stream()
                .filter(
                    node -> language.equals(node.getLanguage()) ||
                        "".equals(node.getLanguage())
                )
                .collect(Collectors.toList());
        final List<Node> nodes = new ArrayList<>();
        for (final Statement<Node> descriptor : related) {
            final Node node = descriptor.getRule();
            nodes.add(node);
        }
        checkDuplicateNodes(nodes);
        return nodes;
    }

    /**
     * Sorts the list of nodes by their depth in inheritance.
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
                } else {

                }
            }
        }
        return new ArrayList<>();
    }

    private HashMap<Node, Integer> countDepth() {
        final HashMap<Node, Integer> depth = new HashMap<>();
        for (final Node node : nodes) {
            final List<Child> composition = node.getComposition();
            for (final Child child : composition) {
                if (child instanceof Disjunction) {
                    final List<Descriptor> desc = ((Disjunction) child).getDescriptors();
                } else if (child instanceof Descriptor) {
                    depth.put(node, 1);
                }
            }
        }
        return depth;
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

    private static void checkDuplicateInheritance(
            final Child child,
            final Set<String> types
    ) throws ExpectedOnlyOneEntity {
        final List<Descriptor> descriptors = ((Disjunction) child).getDescriptors();
        for (final Descriptor descriptor : descriptors) {
            if (types.contains(descriptor.getType())) {
                throw new ExpectedOnlyOneEntity(
                    new StringBuilder()
                        .append(descriptor)
                        .append(" should inherit an abstract node once")
                        .toString()
                );
            }
            types.add(descriptor.getType());
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

    public void analyze() throws ExpectedOnlyOneEntity {
        //final HashMap<Node, Integer> depth = new HashMap<>();
        initialProcess();
        for (final Node node : nodes) {
            if (!info.containsKey(node.getType())) {
                processAbstractNode(node);
            }
        }
    }

    private void processAbstractNode(final Node node) {
        final Result result = new Result(node);
        final List<Child> composition = node.getComposition();
        for (final Child child : composition) {
            if (child instanceof Disjunction) {
                final List<Descriptor> descriptors = ((Disjunction) child).getDescriptors();
                for (final Descriptor descriptor : descriptors) {
//                    if (!info.containsKey(descriptor.getType())) {
//                        stack.push(descriptor.getType());
//                    }
                    processNode(descriptor.getType(), Collections.singletonList(node.getType()));
                }
            }
        }
        info.put(node.getType(), result);
    }

    private void processNode(final String type, final List<String> parents) {
        if (info.containsKey(type)) {
            final Result result = info.get(type);
            result.addParents(parents);
        } else {

        }
    }

    private void initialProcess() throws ExpectedOnlyOneEntity {
        final Set<String> types = new HashSet<>();
        for (final Node node : nodes) {
            final List<Child> composition = node.getComposition();
            for (final Child child : composition) {
                if (child instanceof Disjunction) {
                    checkDuplicateInheritance(child, types);
                } else if (child instanceof Descriptor) {
                    final Descriptor descriptor = (Descriptor) child;
                    final Result result = new Result(node);
                    result.addTaggedChild(
                        descriptor.getTag(),
                        descriptor.getType()
                    );
                    info.put(node.getType(), result);
                }
            }
        }
    }

    public static class Result {
        private final Node node;

        private final List<String> hierarchy;

        private final List<TaggedName> tags;

        public Result(final Node node) {
            this.node = node;
            this.hierarchy = new LinkedList<>();
            this.hierarchy.add(node.getType());
            this.tags = new LinkedList<>();
        }

        public void addParent(final String name) {
            hierarchy.add(name);
        }

        public void addParents(List<String> names) {
            hierarchy.addAll(names);
        }

        public void addTaggedChild(final String tag, final String type) {
            tags.add(new TaggedName(tag, type));
        }

        public List<String> getHierarchy() {
            return this.hierarchy;
        }

        public List<TaggedName> getChildTags() {
            return this.tags;
        }

        public Node getNode() {
            return this.node;
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
