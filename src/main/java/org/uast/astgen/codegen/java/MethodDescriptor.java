/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.LinkedList;
import java.util.List;
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
     * The list of arguments.
     */
    private final List<Argument> arguments;

    /**
     * The type that the method returns.
     */
    private String rettype;

    /**
     * Returns description.
     */
    private String retdescr;

    /**
     * Constructor.
     */
    public MethodDescriptor() {
        this.brief = "Description";
        this.arguments = new LinkedList<>();
        this.rettype = MethodDescriptor.VOID_TYPE;
    }

    /**
     * Sets the new description.
     * @param str The new description
     */
    public void setBriefDescription(final String str) {
        this.brief = Objects.requireNonNull(str);
    }

    /**
     * Adds the argument to the descriptor.
     * @param type The type
     * @param name The name
     * @param description The descriptor
     */
    public void addArgument(final String type, final String name, final String description) {
        this.arguments.add(new Argument(type, name, description));
    }

    /**
     * Sets the return type.
     * @param type The type name
     * @param description The description what the method returns
     */
    public void setReturnType(final String type, final String description) {
        this.rettype = Objects.requireNonNull(type);
        this.retdescr = Objects.requireNonNull(description);
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
        for (final Argument arg : this.arguments) {
            builder.append(tabulation)
                .append(" * \u0040param ")
                .append(arg.getName())
                .append(' ')
                .append(arg.getDescription())
                .append('\n');
        }
        if (!MethodDescriptor.VOID_TYPE.equals(this.rettype)) {
            builder.append(tabulation)
                .append(" * \u0040return ")
                .append(this.retdescr)
                .append('\n');
        }
        builder.append(tabulation).append(" */\n");
        return builder.toString();
    }

    @Override
    public String generate(final int indent) {
        return this.genHeader(indent);
    }

    /**
     * The class for describing arguments of the method.
     * @since 1.0
     */
    private static class Argument {
        /**
         * The type.
         */
        private final String type;

        /**
         * The name.
         */
        private final String name;

        /**
         * The description.
         */
        private final String description;

        /**
         * Constructor.
         * @param type The type
         * @param name The name
         * @param description The description
         */
        Argument(final String type, final String name, final String description) {
            this.type = type;
            this.name = name;
            this.description = description;
        }

        /**
         * Returns the type of the argument.
         * @return The type
         */
        public String getType() {
            return this.type;
        }

        /**
         * Returns the name of the argument.
         * @return The name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Returns the description of the argument.
         * @return The description
         */
        public String getDescription() {
            return this.description;
        }
    }
}
