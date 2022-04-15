/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;

/**
 * Constructs classes, fields and methods for rules that describe nodes.
 *
 * @since 1.0
 */
public abstract class NodeConstructor {
    /**
     * The environment.
     */
    private final Environment env;

    /**
     * The rule.
     */
    private final Node rule;

    /**
     * The class to be filled.
     */
    private final Klass klass;

    /**
     * Flag indicating that the class has been full.
     */
    private boolean flag;

    /**
     * Constructor.
     * @param env The environment
     * @param rule The rule
     * @param klass The class to be filled
     */
    NodeConstructor(final Environment env, final Node rule, final Klass klass) {
        this.env = env;
        this.rule = rule;
        this.klass = klass;
        this.flag = false;
    }

    /**
     * Runs the constructor.
     */
    public void run() {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        this.construct();
    }

    /**
     * Returns the environment.
     * @return The environment
     */
    protected Environment getEnv() {
        return this.env;
    }

    /**
     * Returns the rule.
     * @return The rule
     */
    protected Node getRule() {
        return this.rule;
    }

    /**
     * Returns the class to be filled.
     * @return The class
     */
    protected Klass getKlass() {
        return this.klass;
    }

    /**
     * Constructs the class that describe node.
     */
    protected abstract void construct();
}
