/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

/**
 * The descriptor attribute.
 *
 * @since 1.0
 */
public enum DescriptorAttribute {
    /**
     * Descriptor has no attribute.
     */
    NONE,

    /**
     * Element is optional.
     */
    OPTIONAL,

    /**
     * Element is a list of elements.
     */
    LIST,

    /**
     * Element is a flag that indicates extension.
     */
    EXT
}
