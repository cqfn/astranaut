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
package org.cqfn.astranaut.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.rules.Data;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Hole;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.rules.StringData;

/**
 * Matcher that works with the raw descriptor.
 *
 * @since 0.1.5
 */
public final class Matcher implements org.cqfn.astranaut.base.Matcher {
    /**
     * The descriptor.
     */
    private final Descriptor descriptor;

    /**
     * The list of nested matchers.
     */
    private final Matcher[] subs;

    /**
     * Constructor.
     * @param descriptor The descriptor
     */
    public Matcher(final Descriptor descriptor) {
        this.descriptor = descriptor;
        this.subs = new Matcher[this.descriptor.getParameters().size()];
    }

    @Override
    public boolean match(final Node node, final Map<Integer, List<Node>> children,
        final Map<Integer, String> data) {
        return this.checkType(node) && this.checkChildCount(node)
            && this.checkAndExtractData(node, data)
            && this.checkAndExtractChildren(node, children, data);
    }

    /**
     * Checks the type matches.
     * @param node The node
     * @return Checking result, {@code true} if the type matches
     */
    private boolean checkType(final Node node) {
        return node.getType().getName().equals(this.descriptor.getType());
    }

    /**
     * Checks the number of child nodes matches.
     * @param node The node
     * @return Checking result, {@code true} if the number of child nodes matches
     */
    private boolean checkChildCount(final Node node) {
        final boolean result;
        if (this.descriptor.hasEllipsisHole()) {
            result = true;
        } else {
            result = node.getChildCount() == this.descriptor.getParameters().size();
        }
        return result;
    }

    /**
     * Checks the data matches, extracts the data.
     * @param node The node
     * @param extracted The collection for saving extracted data
     * @return Checking result, {@code true} if the data matches
     */
    private boolean checkAndExtractData(final Node node, final Map<Integer, String> extracted) {
        final Data data = this.descriptor.getData();
        final boolean result;
        if (data instanceof StringData) {
            result = node.getData().equals(((StringData) data).getValue());
        } else if (data instanceof Hole) {
            extracted.put(((Hole) data).getValue(), node.getData());
            result = true;
        } else {
            result = node.getData().isEmpty();
        }
        return result;
    }

    /**
     * Checks the child nodes matches, extracts the children.
     * @param node The node
     * @param children The collection for saving extracted children
     * @param data The collection for saving extracted data
     * @return Checking result, {@code true} if the data matches
     */
    private boolean checkAndExtractChildren(final Node node, final Map<Integer,
        List<Node>> children, final Map<Integer, String> data) {
        boolean result = true;
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Hole) {
                final Hole hole = (Hole) parameter;
                switch (hole.getAttribute()) {
                    case NONE:
                        children.put(
                            hole.getValue(),
                            Collections.singletonList(node.getChild(index))
                        );
                        break;
                    case ELLIPSIS:
                        final int count = node.getChildCount();
                        final List<Node> list = new ArrayList<>(count - index);
                        for (int position = index; position < count; position += 1) {
                            list.add(node.getChild(position));
                        }
                        children.put(hole.getValue(), list);
                        break;
                    default:
                        break;
                }
            } else if (parameter instanceof Descriptor) {
                final Matcher matcher;
                if (this.subs[index] == null) {
                    matcher = new Matcher((Descriptor) parameter);
                    this.subs[index] = matcher;
                } else {
                    matcher = this.subs[index];
                }
                result = matcher.match(node.getChild(index), children, data);
                if (!result) {
                    break;
                }
            }
            index = index + 1;
        }
        return result;
    }
}
