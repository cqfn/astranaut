/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.uast.astgen.rules.Node;

/**
 * Environment for test purposes.
 *
 * @since 1.0
 */
final class TestEnvironment implements Environment {
    /**
     * The license.
     */
    private final License license;

    /**
     * The map of nodes.
     */
    private final Map<String, Node> nodes;

    /**
     * Base constructor.
     * @param nodes The list of nodes
     */
    TestEnvironment(final List<Node> nodes) {
        this.license = new License("LICENSE_header.txt");
        this.nodes = Collections.unmodifiableMap(TestEnvironment.createNodeMap(nodes));
    }

    /**
     * Constructor.
     */
    TestEnvironment() {
        this(Collections.emptyList());
    }

    @Override
    public License getLicense() {
        return this.license;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getRootPackage() {
        return "org.uast.example";
    }

    @Override
    public String getBasePackage() {
        return "org.uast.uast.base";
    }

    @Override
    public boolean isTestMode() {
        return false;
    }

    @Override
    public List<String> getHierarchy(final String name) {
        return Collections.singletonList(name);
    }

    @Override
    public List<TaggedChild> getTags(final String type) {
        List<TaggedChild> result = Collections.emptyList();
        if (this.nodes.containsKey(type)) {
            result = this.nodes.get(type).getTags();
        }
        return result;
    }

    /**
     * Creates map of nodes from list of nodes.
     * @param list List of nodes
     * @return Map of nodes
     */
    private static Map<String, Node> createNodeMap(final List<Node> list) {
        final Map<String, Node> result = new TreeMap<>();
        list.forEach(node -> result.put(node.getType(), node));
        return result;
    }
}
