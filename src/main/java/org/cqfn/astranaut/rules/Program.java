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

package org.cqfn.astranaut.rules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The set of DSL rules with addition data.
 *
 * @since 0.1.5
 */
public class Program {
    /**
     * All rules.
     */
    private final List<Instruction<Rule>> all;

    /**
     * Node descriptors.
     */
    private final List<Instruction<Node>> nodes;

    /**
     * Literal descriptors.
     */
    private final List<Instruction<Literal>> literals;

    /**
     * Vertices (Nodes and Literals) descriptors.
     */
    private final List<Instruction<Vertex>> vertices;

    /**
     * Node descriptors.
     */
    private final List<Instruction<Transformation>> transforms;

    /**
     * Constructor.
     */
    public Program() {
        this.all = new LinkedList<>();
        this.nodes = new LinkedList<>();
        this.literals = new LinkedList<>();
        this.vertices = new LinkedList<>();
        this.transforms = new LinkedList<>();
    }

    /**
     * Returns list of all rules with addition data.
     * @return All rules.
     */
    public List<Instruction<Rule>> getAllRules() {
        return Collections.unmodifiableList(this.all);
    }

    /**
     * Returns list of node descriptors with addition data.
     * @return Node descriptors.
     */
    public List<Instruction<Node>> getNodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    /**
     * Returns list of literal descriptors with addition data.
     * @return Literal descriptors.
     */
    public List<Instruction<Literal>> getLiterals() {
        return Collections.unmodifiableList(this.literals);
    }

    /**
     * Returns list of vertex descriptors with addition data.
     * @return Vertex descriptors.
     */
    public List<Instruction<Vertex>> getVertices() {
        return Collections.unmodifiableList(this.vertices);
    }

    /**
     * Returns list of transformation descriptors with addition data.
     * @return Transformation descriptors.
     */
    public List<Instruction<Transformation>> getTransformations() {
        return Collections.unmodifiableList(this.transforms);
    }

    /**
     * Adds vertex descriptor with addition data.
     * @param instruction The node descriptor
     */
    public void addVertexInstruction(final Instruction<Vertex> instruction) {
        this.vertices.add(instruction);
    }

    /**
     * Adds node descriptor with addition data.
     * @param instruction The node descriptor
     */
    public void addNodeInstruction(final Instruction<Node> instruction) {
        this.all.add(instruction.toRuleInstruction());
        this.nodes.add(instruction);
    }

    /**
     * Adds literal descriptor with addition data.
     * @param instruction The literal descriptor
     */
    public void addLiteralInstruction(final Instruction<Literal> instruction) {
        this.all.add(instruction.toRuleInstruction());
        this.literals.add(instruction);
    }

    /**
     * Adds transformation descriptor with addition data.
     * @param instruction The literal descriptor
     */
    public void addTransformInstruction(final Instruction<Transformation> instruction) {
        this.all.add(instruction.toRuleInstruction());
        this.transforms.add(instruction);
    }

    /**
     * Returns the names of all languages described in the DSL program.
     * @return The set of names
     */
    public Set<String> getNamesOfAllLanguages() {
        final Set<String> result = new TreeSet<>();
        for (final Instruction<Rule> instruction : this.all) {
            final String language = instruction.getLanguage();
            if (!language.isEmpty()) {
                result.add(language);
            }
        }
        return result;
    }
}
