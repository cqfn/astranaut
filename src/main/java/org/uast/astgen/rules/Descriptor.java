/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.List;
import java.util.Locale;

/**
 * Descriptor, i.e. name with tag, parameters and data.
 * Example: tag@Name(parameter)
 *
 * @since 1.0
 */
public abstract class Descriptor implements Child, Parameter {
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
     * Returns the label associated with descriptor.
     * The label cannot be an empty string and can be used as a variable name.
     * @return The label
     */
    public abstract String getLabel();

    /**
     * Returns type associated with descriptor.
     * @return The name (can't be {@code null} or empty)
     */
    public abstract String getType();

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
     * Returns the name of the variable to which the node corresponding
     * to this descriptor can be written.
     * @return Variable name
     */
    public String getVariableName() {
        String result = this.getTag();
        if (result.isEmpty()) {
            result = this.getLabel();
        }
        return result.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Returns tag associated with descriptor, started from capital letter.
     * @return The tag
     */
    public String getTagCapital() {
        final String tag = this.getTag();
        return new StringBuilder().append(tag.substring(0, 1).toUpperCase(Locale.ENGLISH))
            .append(tag.substring(1)).toString();
    }

    /**
     * Returns the full name (i.e. tag and name).
     * @return The name (can't be {@code null} or empty)
     */
    public String getFullName() {
        final String tag = this.getTag();
        final String name = this.getType();
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

    /**
     * Checks whether the descriptor has a hole.
     * @return Checking result, {@code true} if the descriptor has at least one hole
     */
    public boolean hasHole() {
        boolean result = false;
        if (this.getData() instanceof Hole) {
            result = true;
        }
        if (!result) {
            for (final Parameter parameter : this.getParameters()) {
                if (parameter instanceof Hole) {
                    result = true;
                    break;
                }
            }
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
        builder.append(this.getType());
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
        if (attribute == DescriptorAttribute.EXT) {
            builder.append('&');
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
