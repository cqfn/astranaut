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
     * Constructor.
     */
    public Program() {
        this.all = new LinkedList<>();
        this.nodes = new LinkedList<>();
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
     * Adds node descriptor with addition data.
     * @param statement The node descriptor
     */
    public void addNodeStmt(final Statement<Node> statement) {
        this.all.add(statement.toRuleStmt());
        this.nodes.add(statement);
    }

    /**
     * Returns the names of all languages described in the DSL program.
     * @return The set of names
     */
    public Set<String> getNamesOfAllLanguages() {
        Set<String> result = new TreeSet<>();
        for (final Statement<Rule> statement : this.all) {
            final String language = statement.getLanguage();
            if (!language.isEmpty()) {
                result.add(language);
            }
        }
        return result;
    }
}
