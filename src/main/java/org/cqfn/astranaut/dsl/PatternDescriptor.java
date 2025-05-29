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
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.codegen.java.LeftSideItemGenerator;
import org.cqfn.astranaut.codegen.java.PatternMatcherGenerator;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Node;

/**
 * Descriptor representing a pattern in the transformation rule.
 *  This descriptor encapsulates the type of the node, the associated data (such as a static string
 *  or hole), and the child nodes that form subtrees, which are either patterns or holes
 *  themselves. This is used to describe the left side of a transformation rule.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class PatternDescriptor implements PatternItem, LeftSideItem {
    /**
     * The type of the node. A pattern is considered matched if the type name matches.
     */
    private final String type;

    /**
     * The data associated with the node. This could be a static string, untyped hole,
     *  or other data descriptor. A pattern is considered matched if the data matches
     *  or is a hole (in which case the data is transferred to the resulting tree).
     */
    private final LeftDataDescriptor data;

    /**
     * The list of child nodes (subtrees) under this node. Each child can be either
     *  a descriptor or a hole. A pattern is considered matched if all of its children are matched.
     *  If a child is a typed hole, then the type is checked and the child is moved to the
     *  resulting subtree. If a child is an untyped hole, then it is moved to the resulting subtree
     *  without checking.
     */
    private final List<PatternItem> children;

    /**
     * The matching compatibility of the pattern descriptor.
     */
    private PatternMatchingMode mode;

    /**
     * Negation flag.
     */
    private boolean negation;

    /**
     * Constructs a new {@code PatternDescriptor} with the specified type, data, and list
     *  of child nodes.
     * @param type The type of the node
     * @param data The data associated with the node
     * @param children The list of child nodes (subtrees) under this node
     */
    public PatternDescriptor(final String type, final LeftDataDescriptor data,
        final List<PatternItem> children) {
        this.type = type;
        this.data = data;
        this.children = Collections.unmodifiableList(children);
        this.mode = PatternMatchingMode.NORMAL;
    }

    /**
     * Returns the type of the node.
     * @return The type of the node
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the data associated with this node.
     * @return The data descriptor of the node
     */
    public LeftDataDescriptor getData() {
        return this.data;
    }

    /**
     * Returns an unmodifiable list of child nodes (subtrees) under this node.
     * @return The list of child nodes
     */
    public List<PatternItem> getChildren() {
        return this.children;
    }

    /**
     * Checks if there are optional or repeated child descriptors.
     * @return Check result, {@code true} if any
     */
    public boolean hasOptionalOrRepeated() {
        boolean found = false;
        for (final PatternItem item : this.children) {
            if (item instanceof LeftSideItem
                && ((LeftSideItem) item).getMatchingMode() != PatternMatchingMode.NORMAL) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public void setMatchingMode(final PatternMatchingMode value) {
        this.mode = value;
    }

    @Override
    public PatternMatchingMode getMatchingMode() {
        return this.mode;
    }

    @Override
    public void setNegationFlag() {
        this.negation = true;
    }

    @Override
    public boolean isNegationFlagSet() {
        return this.negation;
    }

    @Override
    public LeftSideItemGenerator createGenerator() {
        return new PatternMatcherGenerator(this);
    }

    @Override
    public String toString(final boolean full) {
        final String result;
        if (full) {
            result = this.toFullString();
        } else {
            result = this.toShortString();
        }
        return result;
    }

    @Override
    public String toString() {
        return this.toFullString();
    }

    @Override
    public boolean matchNode(final Node node, final Extracted extracted) {
        final boolean matches = (node.belongsToGroup(this.type) && this.matchData(node)
            && this.matchChildren(node, extracted)) ^ this.negation;
        if (matches && this.data instanceof UntypedHole) {
            extracted.addData(((UntypedHole) this.data).getNumber(), node.getData());
        }
        return matches;
    }

    /**
     * Represents the descriptor as a string in short form, without matching mode.
     * @return Short pattern descriptor as a string
     */
    private String toShortString() {
        final StringBuilder builder = new StringBuilder();
        if (this.negation) {
            builder.append('~');
        }
        builder.append(this.type);
        if (this.data != null) {
            builder.append('<').append(this.data.toString()).append('>');
        }
        if (!this.children.isEmpty()) {
            builder.append('(');
            boolean flag = false;
            for (final PatternItem item : this.children) {
                if (flag) {
                    builder.append(", ");
                }
                builder.append(item.toString());
                flag = true;
            }
            builder.append(')');
        }
        return builder.toString();
    }

    /**
     * Represents the descriptor as a string in full form, with matching mode.
     * @return Full pattern descriptor as a string
     */
    private String toFullString() {
        final StringBuilder builder = new StringBuilder();
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append('[');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('{');
        }
        builder.append(this.toShortString());
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append(']');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('}');
        }
        return builder.toString();
    }

    /**
     * Matches the descriptor and data of the node.
     * @param node The node
     * @return Matching result
     */
    private boolean matchData(final Node node) {
        final boolean matches;
        if (this.data instanceof StaticString) {
            final String expected = ((StaticString) this.data).getValue();
            final String actual = node.getData();
            matches = actual.equals(expected);
        } else {
            matches = true;
        }
        return matches;
    }

    /**
     * Matches the child nodes of the passed node with the child nodes of this descriptor.
     * @param node Node to be matched
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if matched
     */
    private boolean matchChildren(final Node node, final Extracted extracted) {
        final boolean matches;
        if (this.children.isEmpty()) {
            matches = node.getChildCount() == 0;
        } else {
            matches = this.matchNonEmptyChildren(node, extracted);
        }
        return matches;
    }

    /**
     * Matches the child nodes of the passed node with the child nodes of this descriptor.
     *  The list of children of the descriptor is not empty.
     * @param node Node to be matched
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if matched
     */
    private boolean matchNonEmptyChildren(final Node node, final Extracted extracted) {
        final Deque<Node> queue = new LinkedList<>(node.getChildrenList());
        boolean matches = true;
        for (final PatternItem child : this.children) {
            if (!matches) {
                break;
            }
            if (child instanceof UntypedHole) {
                matches = PatternDescriptor.matchUntypedHole((UntypedHole) child, queue, extracted);
                continue;
            }
            final LeftSideItem lsi = (LeftSideItem) child;
            final PatternMatchingMode pmm = lsi.getMatchingMode();
            if (pmm == PatternMatchingMode.OPTIONAL) {
                PatternDescriptor.matchOptionalNode(lsi, queue, extracted);
            } else if (pmm == PatternMatchingMode.REPEATED) {
                PatternDescriptor.matchRepeatedNode(lsi, queue, extracted);
            } else {
                matches = PatternDescriptor.matchRegularNode(lsi, queue, extracted);
            }
        }
        if (!queue.isEmpty()) {
            matches = false;
        }
        return matches;
    }

    /**
     * Matches the next child node and the untyped hole.
     * @param hole Untyped hole
     * @param queue Queue with nodes not yet processed
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if the queue contained another element and
     *  it was extracted
     */
    private static boolean matchUntypedHole(final UntypedHole hole,
        final Deque<Node> queue, final Extracted extracted) {
        boolean result = false;
        if (!queue.isEmpty()) {
            extracted.addNode(hole.getNumber(), queue.poll());
            result = true;
        }
        return result;
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
        boolean matches = false;
        if (!queue.isEmpty()) {
            final Node node = queue.poll();
            matches = lsi.matchNode(node, extracted);
        }
        return matches;
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
