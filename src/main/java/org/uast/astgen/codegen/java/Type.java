/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.utils.StringUtils;

/**
 * The Java type (interface or class).
 *
 * @since 1.0
 */
public interface Type extends Entity {
    /**
     * Returns brief description of the type.
     * @return The description
     */
    String getBrief();

    /**
     * Returns version of the implementation.
     * @return The version
     */
    String getVersion();

    /**
     * Specifies the version of the implementation.
     * @param version The version
     */
    void setVersion(String version);

    /**
     * Generates the type header.
     * @param builder String builder where to generate
     * @param indent Indentation
     */
    default void generateHeader(final StringBuilder builder, final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.getBrief())
            .append(".\n")
            .append(tabulation)
            .append(" *\n")
            .append(tabulation)
            .append(" * @since ")
            .append(this.getVersion())
            .append('\n')
            .append(tabulation)
            .append(" */\n");
    }
}
