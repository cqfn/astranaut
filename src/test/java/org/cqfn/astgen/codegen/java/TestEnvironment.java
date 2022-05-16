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

package org.cqfn.astgen.codegen.java;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astgen.rules.Node;

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
        this.license = new License("LICENSE.txt");
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
    public String getLanguage() {
        return "";
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

    @Override
    public Set<String> getImports(final String type) {
        return Collections.emptySet();
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
