/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The class to store the result of analysis.
 * @since 1.0
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
     * the tagged names.
     * @param common The common tagged names
     */
    public void setOverriddenTags(final Set<TaggedName> common) {
        for (final TaggedName ancestor : common) {
            final int idx = this.tags.indexOf(ancestor);
            final TaggedName local = this.tags.get(idx);
            local.makeOverridden();
        }
    }
}
