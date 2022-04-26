/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

/**
 * An interface for vertices in AST.
 *
 * @since 1.0
 */
public abstract class Vertex implements Rule, Comparable<Vertex> {
    /**
     * Returns left part of the rule.
     * @return The type name
     */
    public abstract String getType();

    /**
     * Checks if the vertex is an ordinary node, i.e. not abstract and not a list.
     * @return Checking result
     */
    public abstract boolean isOrdinary();

    /**
     * Checks if the vertex is an abstract node.
     * @return Checking result
     */
    public abstract boolean isAbstract();

    /**
     * Checks if the vertex is an terminal (leaf) node.
     * @return Checking result
     */
    public abstract boolean isTerminal();
}
