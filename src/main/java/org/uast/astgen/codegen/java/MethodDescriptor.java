/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.utils.StringUtils;

/**
 * The method descriptor, i.e. short description, name, arguments, and return value.
 *
 * @since 1.0
 */
public final class MethodDescriptor implements Entity {
    /**
     * The brief description.
     */
    private String description;

    /**
     * Constructor.
     */
    public MethodDescriptor() {
        this.description = "Description";
    }

    /**
     * Sets the new description.
     * @param str The new description
     */
    public void setDescription(final String str) {
        this.description = str;
    }

    /**
     * Generates JavaDoc header.
     * @param indent Indentation from the beginning of the line
     * @return JavaDoc header
     */
    public String genHeader(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder();
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.description)
            .append(".\n")
            .append(tabulation)
            .append(" */\n");
        return builder.toString();
    }

    @Override
    public String generate(final int indent) {
        return this.genHeader(indent);
    }
}
