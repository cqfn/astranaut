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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.cqfn.astranaut.codegen.java.RuleGenerator;

/**
 * Transformation descriptor describing the transformation of one or more subtrees into a single
 *  subtree by DSL rule.
 * @since 1.0.0
 */
public final class TransformationDescriptor implements Rule {
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
        return null;
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

    /**
     * Checks the left side of the rule (list of items) for correctness.
     * @param left Left size of the rule as a list of left items
     * @return Unmodifiable list of left items
     */
    private static List<LeftSideItem> checkLeftSide(final List<LeftSideItem> left) {
        if (left.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return Collections.unmodifiableList(left);
    }
}
