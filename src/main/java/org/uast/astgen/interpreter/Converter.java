/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.uast.astgen.base.EmptyTree;
import org.uast.astgen.base.Factory;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Transformation;

/**
 * Converter that checks one rule described in DSL and converts a subtree.
 *
 * @since 1.0
 */
public final class Converter implements org.uast.astgen.base.Converter {
    /**
     * The matcher.
     */
    private final Matcher matcher;

    /**
     * The node creator.
     */
    private final Creator creator;

    /**
     * Constructor.
     * @param rule The transformation rule
     */
    public Converter(final Transformation rule) {
        this.matcher = new Matcher(rule.getLeft());
        this.creator = new Creator(rule.getRight());
    }

    @Override
    public Node convert(final Node node, final Factory factory) {
        Node result = EmptyTree.INSTANCE;
        final Map<Integer, List<Node>> children = new TreeMap<>();
        final Map<Integer, String> data = new TreeMap<>();
        if (this.matcher.match(node, children, data)) {
            result = this.creator.create(factory, children, data);
        }
        return result;
    }
}
