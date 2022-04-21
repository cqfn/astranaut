/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

/**
 * The tagged child description, i.e. construction 'tag@Type'.
 *
 * @since 1.0
 */
public interface TaggedChild {
    /**
     * Returns the tag.
     * @return The tag
     */
    String getTag();

    /**
     * Returns the type.
     * @return The type
     */
    String getType();

    /**
     * Returns the flag if the getter method is overridden in the current class.
     * @return The flag
     */
    boolean isOverridden();
}
