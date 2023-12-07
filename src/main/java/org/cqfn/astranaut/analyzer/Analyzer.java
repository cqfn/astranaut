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

package org.cqfn.astranaut.analyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import org.cqfn.astranaut.exceptions.DuplicateRule;
import org.cqfn.astranaut.exceptions.ExtendedNodeNotFound;
import org.cqfn.astranaut.exceptions.GeneratorException;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Disjunction;
import org.cqfn.astranaut.rules.Empty;
import org.cqfn.astranaut.rules.Extension;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Vertex;

/**
 * Analyzes vertices hierarchy described with DSL.
 *
 * @since 0.1.5
 */
public class Analyzer {
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
     * @param storage The vertex storage
     * @throws DuplicateRule exception if vertices described in rules
     *  contain duplications
     */
    public Analyzer(
        final VertexStorage storage) throws DuplicateRule {
        this.storage = storage;
        this.info = new HashMap<>();
        this.stack = new Stack<>();
    }

    /**
     * The hierarchy of names of groups the vertex type belongs to.
     * @param type The type of the vertex
     * @param language The programming language
     * @return The list of type names, cannot be {@code null}
     */
    public List<String> getHierarchy(final String type, final String language) {
        List<String> hierarchy = Collections.emptyList();
        final Vertex vertex = this.storage.getVertexByType(type, language);
        if (vertex != null) {
            hierarchy = this.info.get(vertex).getHierarchy();
        }
        return hierarchy;
    }

    /**
     * The list of tagged names which the provided node has.
     * @param type The type of the node
     * @param language The programming language
     * @return The list of tagged names, cannot be {@code null}
     */
    public List<TaggedName> getTags(final String type, final String language) {
        List<TaggedName> tags = new LinkedList<>();
        final Vertex vertex = this.storage.getVertexByType(type, language);
        if (vertex != null) {
            tags = this.info.get(vertex).getTaggedNames();
        }
        return tags;
    }

    /**
     * The list of vertex types that should be added to an import block
     *  of the specified node.
     * @param type The type of the vertex
     * @param language The programming language
     * @return The list of type names, cannot be {@code null}
     */
    public Set<String> getImports(final String type, final String language) {
        return this.storage.getVerticesToBeImported(type, language);
    }

    /**
     * Processes language-specific vertices.
     * @param language The programming language
     * @throws GeneratorException exception if vertices described in rules
     *  contain duplications or a node was not found
     */
    public void analyze(final String language) throws GeneratorException {
        final List<Vertex> specific = this.storage.getSpecificVertices(language);
        this.pipeline(specific, language);
    }

    /**
     * Processes green vertices.
     * @throws GeneratorException exception if vertices described in rules
     *  contain duplications or a node was not found
     */
    public void analyzeGreen() throws GeneratorException {
        final List<Vertex> green = this.storage.getGreenVertices();
        this.pipeline(green, "");
    }

    /**
     * Conducts analysis of the provided vertex set:
     * - processes final vertices;
     * - processes abstract nodes.
     * @param vertices The list of vertices to be analyzed
     * @param language The programming language
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    public void pipeline(final List<Vertex> vertices, final String language)
        throws ExtendedNodeNotFound {
        for (final Vertex vertex : vertices) {
            if (vertex.isFinal()) {
                this.processFinalVertex(vertex);
            }
        }
        for (final Vertex vertex : vertices) {
            if (vertex.isAbstract() && !this.info.containsKey(vertex)) {
                this.processAbstractNode((Node) vertex, language, new LinkedList<>());
            }
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
     * @param language The programming language
     * @param ancestors The list of ancestor vertex types
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    private void processAbstractNode(
        final Node node, final String language, final List<String> ancestors)
        throws ExtendedNodeNotFound {
        final List<String> hierarchy = new LinkedList<>();
        hierarchy.add(node.getType());
        final Result result = new Result(hierarchy);
        result.addAncestors(ancestors);
        final List<Descriptor> descriptors =
            ((Disjunction) node.getComposition().get(0)).getDescriptors();
        ((LinkedList<String>) ancestors).addFirst(node.getType());
        int empty = 0;
        Vertex extended = null;
        for (final Descriptor descriptor : descriptors) {
            if (descriptor.equals(Extension.INSTANCE)) {
                extended = this.getExtendedNode(node);
                this.processAbstractNodeExtension(extended, ancestors, result);
            } else if (!descriptor.equals(Empty.INSTANCE)) {
                this.processVertex(descriptor.getType(), language, ancestors);
                if (this.storage.getVertexByType(descriptor.getType(), language) == null) {
                    empty += 1;
                }
            }
        }
        ((LinkedList<String>) ancestors).removeFirst();
        if (!this.stack.empty()) {
            this.processCommonTags(descriptors.size() - empty, result, extended);
            if (result.containsTags()) {
                this.stack.push(node);
            }
        }
        this.info.put(node, result);
    }

    /**
     * Processes dependencies of a language-specific abstract node that extends a green
     * abstract node.
     * @param extended The abstract green node that was extended by the current node
     * @param ancestors The list of ancestor vertex types
     * @param result The result of the analysis
     */
    private void processAbstractNodeExtension(
        final Vertex extended,
        final List<String> ancestors,
        final Result result) {
        final List<String> extension = this.getHierarchyOfExtendedNode(extended);
        ancestors.addAll(extension);
        result.addAncestors(extension);
        if (!this.stack.contains((Node) extended)) {
            this.stack.push((Node) extended);
        }
    }

    /**
     * Returns the hierarchy of the abstract green node that was extended
     *  by a language-specific node.
     * @param ancestor The abstract green node
     * @return The list of ancestor vertex types
     */
    private List<String> getHierarchyOfExtendedNode(final Vertex ancestor) {
        final Result result = this.info.get(ancestor);
        return result.getHierarchy();
    }

    /**
     * Returns the abstract green node that was extended
     *  by a language-specific node.
     * @param node The abstract language-specific node
     * @return The extended green node
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    private Vertex getExtendedNode(final Node node)
        throws ExtendedNodeNotFound {
        Vertex result = null;
        final Optional<Vertex> optional = this.storage.getGreenVertices()
            .stream()
            .filter(item -> node.getType().equals(item.getType()))
            .findFirst();
        if (optional.isPresent() && optional.get().isAbstract()) {
            if (this.info.containsKey(optional.get())) {
                result = optional.get();
            }
        } else {
            throw new ExtendedNodeNotFound(node.getType());
        }
        return result;
    }

    /**
     * Finds and processes common tags of an abstract node and its descendants:
     * - iterates over nodes in stack to collect common tagged names;
     * - if processed nodes count equals descendants size, update
     *  {@code overridden} fields and add tags to the ancestor.
     * @param count The count of described descendant nodes
     * @param ancestor The result of ancestor analysis
     * @param extended The abstract green node that was extended by the current node
     */
    private void processCommonTags(final int count, final Result ancestor, final Vertex extended) {
        final Set<Node> related = new HashSet<>();
        final Set<TaggedName> common = new HashSet<>();
        final int idx = this.findCommonTags(count, related, common);
        if (count == idx) {
            for (final Node node : related) {
                final Result result = this.info.get(node);
                if (node.equals(extended) && common.isEmpty()) {
                    result.removeTags();
                    this.updateExtendedNodeDescendants(extended, common);
                } else {
                    result.setOverriddenTags(common);
                }
            }
            for (final TaggedName name : common) {
                ancestor.addTaggedName(name.getTag(), name.getType());
            }
        }
    }

    /**
     * Iterates over nodes in stack to collect common tagged names.
     * @param count The count of described descendant nodes
     * @param related The set of nodes to be updated after finding common tagged names
     * @param common The set of found common tagged names
     * @return The amount of processed nodes
     */
    private int findCommonTags(
        final int count,
        final Set<Node> related,
        final Set<TaggedName> common) {
        int idx = 0;
        while (!this.stack.empty() && idx < count) {
            idx += 1;
            final Node node = this.stack.pop();
            final Result result = this.info.get(node);
            final List<TaggedName> names = result.getTaggedNames();
            if (idx == 1) {
                common.addAll(names);
            } else {
                common.removeIf(name -> !names.contains(name));
            }
            related.add(node);
        }
        return idx;
    }

    /**
     * Updates tags of the descendant nodes of the abstract green node that was extended
     * in other language.
     * @param extended The abstract green node that was extended by other abstract node
     *  in some language
     * @param tags The common tagged names
     */
    private void updateExtendedNodeDescendants(
        final Vertex extended, final Set<TaggedName> tags) {
        final List<Vertex> descendants = new VerticesProcessor(this.storage.getGreenVertices())
            .getDescendantVerticesByType((Node) extended);
        for (final Vertex descendant : descendants) {
            final Result result = this.info.get(descendant);
            result.setOverriddenTags(tags);
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
     * @param language The programming language
     * @param ancestors The list of ancestor node types
     * @throws ExtendedNodeNotFound exception if extended green node
     *  was not found
     */
    private void processVertex(
        final String type, final String language, final List<String> ancestors)
        throws ExtendedNodeNotFound {
        final Vertex vertex = this.storage.getVertexByType(type, language);
        if (vertex != null && this.info.containsKey(vertex)) {
            final Result result = this.info.get(vertex);
            result.addAncestors(ancestors);
            if (result.containsTags() && !this.stack.contains(vertex)) {
                this.stack.push((Node) vertex);
            }
        } else if (vertex != null && vertex.isAbstract()) {
            this.processAbstractNode((Node) vertex, language, ancestors);
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
}
