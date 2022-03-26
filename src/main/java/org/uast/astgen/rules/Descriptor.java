/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.List;

/**
 * Descriptor, i.e. name with tag, parameters and data.
 * Example: tag@Name(parameter)
 *
 * @since 1.0
 */
public abstract class Descriptor implements Parameter {
    /**
     * Returns the attribute of descriptor.
     * @return The attribute
     */
    public abstract DescriptorAttribute getAttribute();

    /**
     * Returns tag associated with descriptor.
     * @return The tag (or empty string).
     */
    public abstract String getTag();

    /**
     * Returns name associated with descriptor.
     * @return The name (can't be {@code null} or empty)
     */
    public abstract String getName();

    /**
     * Returns list of parameters inside descriptor.
     * @return List of parameters
     */
    public abstract List<Parameter> getParameters();

    /**
     * Returns data associated with descriptor.
     * @return Data
     */
    public abstract Data getData();

    /**
     * Returns the full name (i.e. tag and name).
     * @return The name (can't be {@code null} or empty)
     */
    public String getFullName() {
        final String tag = this.getTag();
        final String name = this.getName();
        final String result;
        if (tag.isEmpty()) {
            result = name;
        } else {
            result = new StringBuilder()
                .append(tag)
                .append('@')
                .append(name)
                .toString();
        }
        return result;
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        final DescriptorAttribute attribute = this.getAttribute();
        if (attribute == DescriptorAttribute.OPTIONAL) {
            builder.append('[');
        } else if (attribute == DescriptorAttribute.LIST) {
            builder.append('{');
        }
        final String tag = this.getTag();
        if (!tag.isEmpty()) {
            builder.append(tag).append('@');
        }
        builder.append(this.getName());
        this.parametersToString(builder);
        final Data data = this.getData();
        if (data.isValid()) {
            builder.append('<').append(data.toString()).append('>');
        }
        if (attribute == DescriptorAttribute.OPTIONAL) {
            builder.append(']');
        } else if (attribute == DescriptorAttribute.LIST) {
            builder.append('}');
        }
        return builder.toString();
    }

    /**
     * Builds parameters list as a string.
     * @param builder String builder where to build.
     */
    private void parametersToString(final StringBuilder builder) {
        final List<Parameter> parameters = this.getParameters();
        if (!parameters.isEmpty()) {
            boolean flag = false;
            builder.append('(');
            for (final Parameter parameter : parameters) {
                if (flag) {
                    builder.append(", ");
                }
                flag = true;
                builder.append(parameter.toString());
            }
            builder.append(')');
        }
    }
}
