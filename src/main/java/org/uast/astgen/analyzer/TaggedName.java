/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import java.util.Objects;

/**
 * The class to store tagged names.
 *
 * @since 1.0
 */
public final class TaggedName {
    /**
     * The tag.
     */
    private final String tag;

    /**
     * The type.
     */
    private final String type;

    /**
     * The flag indicates that the tag is overridden.
     */
    private boolean overridden;

    /**
     * Constructor.
     * @param tag The tag
     * @param type The type
     */
    TaggedName(final String tag, final String type) {
        this.tag = tag;
        this.type = type;
        this.overridden = false;
    }

    /**
     * Makes the tagged name overridden.
     */
    public void makeOverridden() {
        this.overridden = true;
    }

    /**
     * Returns the tag.
     * @return The tag
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Returns the type.
     * @return The type
     */
    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder
            .append('{')
            .append(this.tag)
            .append(", ")
            .append(this.type)
            .append(", ")
            .append(this.overridden)
            .append('}');
        return builder.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        final TaggedName name;
        boolean equal = false;
        if (obj instanceof TaggedName) {
            name = (TaggedName) obj;
            if (this.type.equals(name.getType())
                && this.tag.equals(name.getTag())) {
                equal = true;
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tag + this.type);
    }
}
