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

package org.cqfn.astranaut.analyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The class to store the result of analysis.
 * @since 0.1.5
 */
public class Result {
    /**
     * The node hierarchy (its type and types of its ancestors)
     * sorted by increasing depth of inheritance.
     */
    private final List<String> hierarchy;

    /**
     * The list of tagged names.
     */
    private final List<TaggedName> tags;

    /**
     * Constructor.
     * @param hierarchy The hierarchy list
     */
    Result(final List<String> hierarchy) {
        this.hierarchy = hierarchy;
        this.tags = new LinkedList<>();
    }

    /**
     * Adds ancestor types to the hierarchy.
     * @param names The list of ancestor type names
     */
    public void addAncestors(final List<String> names) {
        this.hierarchy.addAll(names);
    }

    /**
     * Adds a tagged type name.
     * @param tag The tag
     * @param type The type
     */
    public void addTaggedName(final String tag, final String type) {
        this.tags.add(new TaggedName(tag, type));
    }

    /**
     * Returns the hierarchy of the node.
     * @return The list of types
     */
    public List<String> getHierarchy() {
        return this.hierarchy;
    }

    /**
     * Returns the hierarchy of the node.
     * @return The list of types
     */
    public List<TaggedName> getTaggedNames() {
        return this.tags;
    }

    /**
     * Checks if the node contains tagged names.
     * @return Checking result
     */
    public boolean containsTags() {
        return !this.tags.isEmpty();
    }

    /**
     * Sets the {@code true} value to the {@code overridden} property of
     * the tagged names or sets the {@code false} if there are no common tags.
     * @param common The common tagged names
     */
    public void setOverriddenTags(final Set<TaggedName> common) {
        if (common.isEmpty()) {
            for (final TaggedName ancestor : this.getTaggedNames()) {
                ancestor.makeNotOverridden();
            }
        } else {
            for (final TaggedName ancestor : common) {
                final int idx = this.tags.indexOf(ancestor);
                final TaggedName local = this.tags.get(idx);
                local.makeOverridden();
            }
        }
    }

    /**
     * Removes tags.
     */
    public void removeTags() {
        this.tags.clear();
    }
}
