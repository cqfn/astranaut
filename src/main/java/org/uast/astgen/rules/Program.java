/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.rules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The set of DSL rules with addition data.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public class Program {
    /**
     * All rules.
     */
    private final List<Statement<Rule>> all;

    /**
     * Node descriptors.
     */
    private final List<Statement<Node>> nodes;

    /**
     * Literal descriptors.
     */
    private final List<Statement<Literal>> literals;

    /**
     * Vertices (Nodes and Literals) descriptors.
     */
    private final List<Statement<Vertex>> vertices;

    /**
     * Node descriptors.
     */
    private final List<Statement<Transformation>> transforms;

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
    public List<Statement<Rule>> getAllRules() {
        return Collections.unmodifiableList(this.all);
    }

    /**
     * Returns list of node descriptors with addition data.
     * @return Node descriptors.
     */
    public List<Statement<Node>> getNodes() {
        return Collections.unmodifiableList(this.nodes);
    }

    /**
     * Returns list of literal descriptors with addition data.
     * @return Literal descriptors.
     */
    public List<Statement<Literal>> getLiterals() {
        return Collections.unmodifiableList(this.literals);
    }

    /**
     * Returns list of vertex descriptors with addition data.
     * @return Vertex descriptors.
     */
    public List<Statement<Vertex>> getVertices() {
        return Collections.unmodifiableList(this.vertices);
    }

    /**
     * Returns list of transformation descriptors with addition data.
     * @return Transformation descriptors.
     */
    public List<Statement<Transformation>> getTransformations() {
        return Collections.unmodifiableList(this.transforms);
    }

    /**
     * Adds vertex descriptor with addition data.
     * @param statement The node descriptor
     */
    public void addVertexStmt(final Statement<Vertex> statement) {
        this.vertices.add(statement);
    }

    /**
     * Adds node descriptor with addition data.
     * @param statement The node descriptor
     */
    public void addNodeStmt(final Statement<Node> statement) {
        this.all.add(statement.toRuleStmt());
        this.nodes.add(statement);
    }

    /**
     * Adds literal descriptor with addition data.
     * @param statement The literal descriptor
     */
    public void addLiteralStmt(final Statement<Literal> statement) {
        this.all.add(statement.toRuleStmt());
        this.literals.add(statement);
    }

    /**
     * Adds transformation descriptor with addition data.
     * @param statement The literal descriptor
     */
    public void addTransformStmt(final Statement<Transformation> statement) {
        this.all.add(statement.toRuleStmt());
        this.transforms.add(statement);
    }

    /**
     * Returns the names of all languages described in the DSL program.
     * @return The set of names
     */
    public Set<String> getNamesOfAllLanguages() {
        final Set<String> result = new TreeSet<>();
        for (final Statement<Rule> statement : this.all) {
            final String language = statement.getLanguage();
            if (!language.isEmpty()) {
                result.add(language);
            }
        }
        return result;
    }
}
