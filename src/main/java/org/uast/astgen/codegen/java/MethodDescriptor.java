/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Objects;
import org.uast.astgen.utils.StringUtils;

/**
 * The method descriptor, i.e. short description, name, arguments, and return value.
 *
 * @since 1.0
 */
public final class MethodDescriptor implements Entity {
    /**
     * The 'void' type.
     */
    private static final String VOID_TYPE = "void";

    /**
     * The brief description.
     */
    private String brief;

    /**
     * The type that the method returns.
     */
    private String rtype;

    /**
     * Returns description.
     */
    private String rdescr;

    /**
     * Constructor.
     */
    public MethodDescriptor() {
        this.brief = "Description";
        this.rtype = MethodDescriptor.VOID_TYPE;
    }

    /**
     * Sets the new description.
     * @param str The new description
     */
    public void setBriefDescription(final String str) {
        this.brief = Objects.requireNonNull(str);
    }

    /**
     * Sets the return type.
     * @param type The type name
     * @param description The description what the method returns
     */
    public void setReturnType(final String type, final String description) {
        this.rtype = Objects.requireNonNull(type);
        this.rdescr = Objects.requireNonNull(description);
    }

    /**
     * Generates JavaDoc header.
     * @param indent Indentation from the beginning of the line
     * @return JavaDoc header
     */
    public String genHeader(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(32);
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.brief)
            .append(".\n");
        if (!MethodDescriptor.VOID_TYPE.equals(this.rtype)) {
            builder.append(tabulation)
                .append(" * \u0040return ")
                .append(this.rdescr)
                .append('\n');
        }
        builder.append(tabulation).append(" */\n");
        return builder.toString();
    }

    @Override
    public String generate(final int indent) {
        return this.genHeader(indent);
    }
}
