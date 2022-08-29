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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.cqfn.astranaut.exceptions.DuplicateRule;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Disjunction;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Vertex;

/**
 * Stores vertices to be analyzed and checks their validity.
 *
 * @since 0.1.5
 */
public class VertexStorage {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(VertexStorage.class.getName());

    /**
     * The vertex descriptors.
     */
    private final List<Instruction<Vertex>> descriptors;

    /**
     * The names of programming languages under process.
     */
    private final Set<String> languages;

    /**
     * Vertices related to green nodes.
     */
    private List<Vertex> green;

    /**
     * Vertices related to the specified programming language.
     */
    private final Map<String, List<Vertex>> red;

    /**
     * Constructor.
     * @param descriptors The vertex descriptors
     * @param languages The list of programming languages under consideration
     */
    public VertexStorage(
        final List<Instruction<Vertex>> descriptors,
        final Set<String> languages) {
        this.descriptors = descriptors;
        this.languages = languages;
        this.green = new LinkedList<>();
        this.red = new HashMap<>();
    }

    /**
     * Retrieves the list of vertices related to the selected language and green vertices,
     * checks for redundant vertices and sorts the list of vertices.
     * @throws DuplicateRule exception if vertices described in rules
     *  contain duplications
     */
    public void collectAndCheck() throws DuplicateRule {
        final List<Vertex> common = new LinkedList<>();
        final Map<String, List<Vertex>> target = new TreeMap<>();
        for (final Instruction<Vertex> instruction : this.descriptors) {
            if (instruction.getLanguage().isEmpty()) {
                common.add(instruction.getRule());
            } else if (this.languages.contains(instruction.getLanguage())) {
                final List<Vertex> list =
                    target.getOrDefault(instruction.getLanguage(), new LinkedList<>());
                list.add(instruction.getRule());
                target.put(instruction.getLanguage(), list);
            }
        }
        checkDuplicateVertices(common);
        checkDuplicateInheritance(common, Collections.emptyList());
        for (final String lang : target.keySet()) {
            checkDuplicateVertices(target.get(lang));
            checkDuplicateInheritance(common, target.get(lang));
        }
        final VertexSorter sorter = new VertexSorter(common);
        this.green = Collections.unmodifiableList(sorter.sortVertices());
        for (final String lang : target.keySet()) {
            this.red.put(lang, target.get(lang));
        }
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
     * @param language The name of the language
     * @return The list of vertices related to the specified
     *  programming language
     */
    public List<Vertex> getSpecificVertices(final String language) {
        return this.red.getOrDefault(language, Collections.emptyList());
    }

    /**
     * Returns types of vertices which should be imported into the generated class
     * of the specified node.
     * @param type The vertex type
     * @param language The programming language
     * @return The list of vertex types
     */
    public Set<String> getVerticesToBeImported(final String type, final String language) {
        final Set<String> imports = new LinkedHashSet<>();
        List<Vertex> list = Collections.emptyList();
        if (!language.isEmpty()) {
            list = this.red.get(language);
        }
        final Optional<Vertex> optional =
            list.stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
        if (optional.isPresent() && optional.get().isOrdinary()) {
            final Node node = (Node) optional.get();
            final List<Child> children = node.getComposition();
            for (final Child child : children) {
                final Descriptor descriptor = (Descriptor) child;
                final String name = descriptor.getType();
                if (!this.isInSpecificVertices(name, language) && this.isInGreenVertices(name)) {
                    imports.add(name);
                }
            }
        }
        return imports;
    }

    /**
     * Gets a vertex from the list of green or the list of language-specific vertices.
     * @param type The vertex type
     * @param language The programming language
     * @return The vertex
     */
    public Vertex getVertexByType(final String type, final String language) {
        final Optional<Vertex> optional;
        if (language.isEmpty()) {
            optional = this.green.stream()
                .filter(item -> type.equals(item.getType()))
                .findFirst();
        } else {
            optional = this.red.get(language).stream()
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
     * @param language The programming language
     * @return Checking result
     */
    private boolean isInSpecificVertices(final String type, final String language) {
        boolean result = false;
        final List<Vertex> list = this.red.get(language);
        if (list != null) {
            result = list.stream()
                .anyMatch(item -> type.equals(item.getType()));
        }
        return result;
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
     * @param common The list of green vertices
     * @param local The list of language-specific vertices
     * @throws DuplicateRule exception if nodes described in rules
     *  contain duplications
     */
    private static void checkDuplicateInheritance(
        final List<Vertex> common,
        final List<Vertex> local) throws DuplicateRule {
        final List<Vertex> related = new LinkedList<>(common);
        related.addAll(local);
        final Set<String> types = new HashSet<>();
        for (final Vertex vertex : related) {
            if (vertex.isAbstract()) {
                final List<Descriptor> descriptors =
                    ((Disjunction) (((Node) vertex).getComposition().get(0))).getDescriptors();
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
}
