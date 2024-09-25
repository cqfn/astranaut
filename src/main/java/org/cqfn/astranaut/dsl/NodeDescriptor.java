/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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
package org.cqfn.astranaut.dsl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.cqfn.astranaut.core.base.Type;

/**
 * Node descriptor. Uniquely identifies the type of node in the node hierarchy
 *  described by DSL rules.
 * @since 1.0.0
 */
public abstract class NodeDescriptor implements Rule, Type {
    /**
     * Name of the type of the node (left side of the rule).
     */
    private final String name;

    /**
     * List of base descriptors, that is, the descriptors of the abstract nodes
     *  from which this node inherits. Multiple inheritance is allowed,
     *  respectively.
     */
    private final List<NodeDescriptor> bases;

    /**
     * Constructor.
     * @param name Name of the type of the node (left side of the rule)
     */
    public NodeDescriptor(final String name) {
        this.name = name;
        this.bases = new ArrayList<>(1);
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final List<String> getHierarchy() {
        return this.getTopology().stream().map(NodeDescriptor::getName)
            .collect(Collectors.toList());
    }

    /**
     * Adds a base descriptor, that is, the descriptor of the abstract node
     *  from which this node inherits.
     * @param descriptor Base descriptor
     */
    public void addBaseDescriptor(final NodeDescriptor descriptor) {
        if (this.checkForCycle(descriptor)) {
            throw new IllegalArgumentException("Adding this descriptor would create a cycle.");
        }
        this.bases.add(descriptor);
    }

    /**
     * Returns the inheritance topology, that is, a list of all descriptors that are base
     *  descriptors for this descriptor, sorted by a topological sorting algorithm.
     * @return A list of descriptors consisting of at least one descriptor (this one)
     */
    public List<NodeDescriptor> getTopology() {
        final List<NodeDescriptor> topology = new LinkedList<>();
        final Set<NodeDescriptor> visited = new HashSet<>();
        this.buildTopology(topology, visited);
        return topology;
    }

    /**
     * Checks that the new base descriptor being added will not create a loop
     *  in the inheritance hierarchy, which is not allowed.
     * @param descriptor Base descriptor
     * @return Checking result ({@code  true} if adding a new descriptor will make a loop)
     */
    private boolean checkForCycle(final NodeDescriptor descriptor) {
        boolean result = false;
        if (descriptor == this) {
            result = true;
        } else {
            for (final NodeDescriptor base : descriptor.bases) {
                result = this.checkForCycle(base);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Recursively performs topological sorting of the inheritance graph.
     * @param topology List to which the sorted descriptors will be added
     * @param visited Visited descriptors, it is necessary not to process them repeatedly
     */
    private void buildTopology(final List<NodeDescriptor> topology,
        final Set<NodeDescriptor> visited) {
        if (!visited.contains(this)) {
            for (final NodeDescriptor base : this.bases) {
                base.buildTopology(topology, visited);
            }
            topology.add(0, this);
            visited.add(this);
        }
    }
}
