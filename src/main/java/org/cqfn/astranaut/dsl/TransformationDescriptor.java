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

import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.codegen.java.TransformationGenerator;
import org.cqfn.astranaut.core.algorithms.conversion.ConversionResult;
import org.cqfn.astranaut.core.algorithms.conversion.Converter;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Factory;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;

/**
 * Transformation descriptor describing the transformation of one or more subtrees into a single
 *  subtree by DSL rule.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class TransformationDescriptor implements Rule, Converter {
    /**
     * Left side of the rule, that is, at least one pattern or typed hole.
     */
    private final List<LeftSideItem> left;

    /**
     * Right side of the rule, that is, the description of the resulting subtree.
     */
    private final RightSideItem right;

    /**
     * Name of the programming language for which this transformation descriptor is described.
     */
    private String language;

    /**
     * Set of nodes on which this node depends. These can be child or base node types.
     */
    private final Set<NodeDescriptor> dependencies;

    /**
     * Constructor.
     * @param left Left side of the rule, that is, at least one pattern or typed hole
     * @param right Right side of the rule, that is, the description of the resulting subtree
     */
    public TransformationDescriptor(final List<LeftSideItem> left, final RightSideItem right) {
        this.left = TransformationDescriptor.checkLeftSide(left);
        this.right = right;
        this.language = "common";
        this.dependencies = new HashSet<>();
    }

    /**
     * Returns left side of the rule, that is, at least one pattern or typed hole.
     * @return List of left descriptors
     */
    public List<LeftSideItem> getLeft() {
        return this.left;
    }

    /**
     * Returns right side of the rule.
     * @return A hole or description of the resulting subtree.
     */
    public RightSideItem getRight() {
        return this.right;
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    /**
     * Sets the name of the programming language for which this transformation descriptor
     *  is described.
     * @param value Name of the programming language
     */
    public void setLanguage(final String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.language = value.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Checks if the left side of this transformation rule contains
     *  optional or repeated descriptors.
     * @return Check result, {@code true} if any
     */
    public boolean hasOptionalOrRepeated() {
        boolean found = false;
        for (final LeftSideItem item : this.left) {
            if (item.getMatchingMode() != PatternMatchingMode.NORMAL) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public void addDependency(final NodeDescriptor descriptor) {
        this.dependencies.add(descriptor);
    }

    @Override
    public Set<NodeDescriptor> getDependencies() {
        return Collections.unmodifiableSet(this.dependencies);
    }

    @Override
    public RuleGenerator createGenerator() {
        return new TransformationGenerator(this);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        boolean flag = false;
        for (final LeftSideItem item : this.left) {
            if (flag) {
                builder.append(", ");
            }
            flag = true;
            builder.append(item.toString());
        }
        builder.append(" -> ").append(this.right.toString());
        return builder.toString();
    }

    @Override
    public Optional<ConversionResult> convert(final List<Node> list, final int index,
        final Factory factory) {
        Optional<ConversionResult> result = Optional.empty();
        do {
            if (index + this.getMinConsumed() > list.size()) {
                break;
            }
            final Extracted extracted = new Extracted();
            final Deque<Node> queue = new LinkedList<>(list.subList(index, list.size()));
            final int size = queue.size();
            if (!this.matchNodes(queue, extracted)) {
                break;
            }
            final int consumed = size - queue.size();
            final Node node;
            if (this.right instanceof UntypedHole) {
                node = extracted.getNodes(((UntypedHole) this.right).getNumber()).get(0);
            } else {
                final ResultingSubtreeDescriptor rsd = (ResultingSubtreeDescriptor) this.right;
                final Fragment fragment = Fragment.fromNodes(list.subList(index, index + consumed));
                node = rsd.createNode(extracted, factory, fragment);
                if (node == DummyNode.INSTANCE) {
                    break;
                }
            }
            result = Optional.of(new ConversionResult(node, consumed));
        } while (false);
        return result;
    }

    @Override
    public int getMinConsumed() {
        return TransformationDescriptor.calcMinConsumed(this.left);
    }

    @Override
    public boolean isRightToLeft() {
        return false;
    }

    /**
     * Checks the left side of the rule (list of items) for correctness.
     * @param left Left size of the rule as a list of left items
     * @return Unmodifiable list of left items
     */
    private static List<LeftSideItem> checkLeftSide(final List<LeftSideItem> left) {
        if (TransformationDescriptor.calcMinConsumed(left) < 1) {
            throw new IllegalArgumentException(
                "At least one node on the left must be guaranteed to be consumed"
            );
        }
        return Collections.unmodifiableList(left);
    }

    /**
     * Calculates the minimum number of elements consumed by this transformation rule,
     *  based on the matching mode of each item.
     * @param list List of left side elements
     * @return Minimum number of consumed elements
     */
    private static int calcMinConsumed(final List<LeftSideItem> list) {
        int consumed = 0;
        for (final LeftSideItem item : list) {
            if (item.getMatchingMode() == PatternMatchingMode.NORMAL) {
                consumed = consumed + 1;
            }
        }
        if (consumed == 0 && list.size() == 1
            && list.get(0).getMatchingMode() == PatternMatchingMode.REPEATED) {
            consumed = 1;
        }
        return consumed;
    }

    /**
     * Matches nodes with patterns from the left side of the transformation
     *  and extracts nodes and data.
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if the node sequence has been matched
     *  and the data and nodes have been retrieved
     */
    private boolean matchNodes(final Deque<Node> queue, final Extracted extracted) {
        final boolean matches;
        if (this.left.size() == 1
            && this.left.get(0).getMatchingMode() == PatternMatchingMode.REPEATED) {
            matches = this.matchRepeatedPattern(queue, extracted);
        } else {
            matches = this.matchListOfDifferentNodes(queue, extracted);
        }
        return matches;
    }

    /**
     * Special case: matches a repeating pattern, and it must match at least once.
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if the queue contained elements and they
     *  were extracted
     */
    private boolean matchRepeatedPattern(final Deque<Node> queue, final Extracted extracted) {
        final LeftSideItem lsi = this.left.get(0);
        boolean flag = false;
        while (!queue.isEmpty()) {
            final Node node = queue.poll();
            final boolean matches = lsi.matchNode(node, extracted);
            if (!matches) {
                queue.addFirst(node);
                break;
            }
            flag = true;
        }
        return flag;
    }

    /**
     * Matches list of different nodes with patterns from the left side of the transformation
     *  and extracts nodes and data.
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if the node sequence has been matched
     *  and the data and nodes have been retrieved
     */
    private boolean matchListOfDifferentNodes(final Deque<Node> queue, final Extracted extracted) {
        boolean matches = true;
        for (final LeftSideItem lsi : this.left) {
            final PatternMatchingMode pmm = lsi.getMatchingMode();
            if (pmm == PatternMatchingMode.OPTIONAL) {
                TransformationDescriptor.matchOptionalNode(lsi, queue, extracted);
            } else if (pmm == PatternMatchingMode.REPEATED) {
                TransformationDescriptor.matchRepeatedNode(lsi, queue, extracted);
            } else {
                matches = TransformationDescriptor.matchRegularNode(lsi, queue, extracted);
            }
            if (!matches) {
                break;
            }
        }
        return matches;
    }

    /**
     * Matches a "normal" (not optional and not repeated) pattern and the next node.
     * @param lsi Pattern
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if the queue contained another element and
     *  it was extracted
     */
    private static boolean matchRegularNode(final LeftSideItem lsi, final Deque<Node> queue,
        final Extracted extracted) {
        final Node node = queue.poll();
        return lsi.matchNode(node, extracted);
    }

    /**
     * Matches an optional pattern and the next node.
     * @param lsi Pattern
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     */
    private static void matchOptionalNode(final LeftSideItem lsi, final Deque<Node> queue,
        final Extracted extracted) {
        if (queue.isEmpty()) {
            return;
        }
        final Node node = queue.poll();
        final boolean matches = lsi.matchNode(node, extracted);
        if (!matches) {
            queue.addFirst(node);
        }
    }

    /**
     * Matches a repeated pattern and the next node.
     * @param lsi Pattern
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     */
    private static void matchRepeatedNode(final LeftSideItem lsi, final Deque<Node> queue,
        final Extracted extracted) {
        while (!queue.isEmpty()) {
            final Node node = queue.poll();
            final boolean matches = lsi.matchNode(node, extracted);
            if (!matches) {
                queue.addFirst(node);
                break;
            }
        }
    }
}
