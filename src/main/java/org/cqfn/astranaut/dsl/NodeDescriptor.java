/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Node descriptor. Uniquely identifies the type of node in the node hierarchy
 *  described by DSL rules.
 * @since 1.0.0
 */
public abstract class NodeDescriptor implements Rule {
    /**
     * Name of the programming language for which this node descriptor is described.
     */
    private String language;

    /**
     * Name of the type of the node (left side of the rule).
     */
    private final String name;

    /**
     * List of base descriptors, that is, the descriptors of the abstract nodes
     *  from which this node inherits. Multiple inheritance is allowed,
     *  respectively.
     */
    private final List<AbstractNodeDescriptor> bases;

    /**
     * Set of nodes on which this node depends. These can be child or base node types.
     */
    private final Set<NodeDescriptor> dependencies;

    /**
     * Inheritance topology (cached list).
     */
    private List<NodeDescriptor> topology;

    /**
     * Constructor.
     * @param name Name of the type of the node (left side of the rule)
     */
    public NodeDescriptor(final String name) {
        this.language = "common";
        this.name = name;
        this.bases = new ArrayList<>(1);
        this.dependencies = new HashSet<>();
    }

    /**
     * Returns the set of tags that have child nodes and their corresponding descriptors.
     * @return Tags correlated with descriptors
     */
    public abstract Map<String, ChildDescriptorExt> getTags();

    /**
     * Sets the name of the programming language for which this node descriptor is described.
     * @param value Name of the programming language
     */
    public void setLanguage(final String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.language = value.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public final String getLanguage() {
        return this.language;
    }

    @Override
    public final void addDependency(final NodeDescriptor descriptor) {
        this.dependencies.add(descriptor);
    }

    @Override
    public final Set<NodeDescriptor> getDependencies() {
        return Collections.unmodifiableSet(this.dependencies);
    }

    /**
     * Returns the name of the type of the node (i.e. left side of the rule).
     * @return Type name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Adds a base descriptor, that is, the descriptor of the abstract node
     *  from which this node inherits.
     * @param descriptor Base descriptor
     * @throws CycleException If a loop in the inheritance hierarchy occurs when adding
     */
    public void addBaseDescriptor(final AbstractNodeDescriptor descriptor) throws CycleException {
        if (this.checkForCycle(descriptor)) {
            throw new CycleException(descriptor);
        }
        this.bases.add(descriptor);
        this.dependencies.add(descriptor);
    }

    /**
     * Returns list of base descriptors.
     * @return List of node descriptors from which this node inherits
     */
    public List<AbstractNodeDescriptor> getBaseDescriptors() {
        return Collections.unmodifiableList(this.bases);
    }

    /**
     * Returns the inheritance topology, that is, a list of all descriptors that are base
     *  descriptors for this descriptor, sorted by a topological sorting algorithm.
     * @return A list of descriptors consisting of at least one descriptor (this one)
     */
    public List<NodeDescriptor> getTopology() {
        if (this.topology == null) {
            this.topology = new LinkedList<>();
            final Set<NodeDescriptor> visited = new HashSet<>();
            this.buildTopology(this.topology, visited);
        }
        return this.topology;
    }

    /**
     * Checks if the given tag exists in the current descriptor or any of its base descriptors.
     * @param tag The tag to search for
     * @return Checking result, {@code true} if the tag exists, either in the current descriptor
     *  or one of its base descriptors, {@code false} otherwise.
     */
    public boolean hasTag(final String tag) {
        final Map<String, ChildDescriptorExt> tags = this.getTags();
        boolean result = tags.containsKey(tag);
        if (!result) {
            result = this.baseHasTag(tag);
        }
        return result;
    }

    /**
     * Checks if the given tag exists in any of the base descriptors.
     * It iterates over all base descriptors and checks if the tag is present in any of them.
     * @param tag The tag to search for.
     * @return Checking result, {@code true} if the tag exists in any of the base descriptors,
     *  {@code false} otherwise.
     */
    public boolean baseHasTag(final String tag) {
        boolean result = false;
        for (final AbstractNodeDescriptor base : this.bases) {
            if (base.hasTag(tag)) {
                result = true;
                break;
            }
        }
        return result;
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
     * @param list List to which the sorted descriptors will be added
     * @param visited Visited descriptors, it is necessary not to process them repeatedly
     */
    private void buildTopology(final List<NodeDescriptor> list,
        final Set<NodeDescriptor> visited) {
        if (!visited.contains(this)) {
            final ListIterator<AbstractNodeDescriptor> iterator =
                this.bases.listIterator(this.bases.size());
            while (iterator.hasPrevious()) {
                final NodeDescriptor base = iterator.previous();
                base.buildTopology(list, visited);
            }
            list.add(0, this);
            visited.add(this);
        }
    }

    /**
     * Exception: 'Adding this descriptor would create a cycle'.
     * @since 1.0.0
     */
    private static final class CycleException extends BaseException {
        /**
         * Version identifier.
         */
        private static final long serialVersionUID = -1;

        /**
         * Descriptor.
         */
        private final NodeDescriptor descriptor;

        /**
         * Constructor.
         * @param descriptor Descriptor
         */
        private CycleException(final NodeDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public String getInitiator() {
            return "Analyzer";
        }

        @Override
        public String getErrorMessage() {
            return String.format(
                "Adding this descriptor would create a cycle: '%s'",
                this.descriptor.toString()
            );
        }
    }
}
