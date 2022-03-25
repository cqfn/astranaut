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
public abstract class Descriptor {
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
    public abstract List<Descriptor> getParameters();

    /**
     * Returns data associated with descriptor.
     * @return Data
     */
    public abstract Data getData();

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        final String tag = this.getTag();
        if (!tag.isEmpty()) {
            builder.append(tag).append('@');
        }
        builder.append(this.getName());
        final List<Descriptor> parameters = this.getParameters();
        if (!parameters.isEmpty()) {
            boolean flag = false;
            builder.append('(');
            for (final Descriptor parameter : parameters) {
                if (flag) {
                    builder.append(", ");
                }
                flag = true;
                builder.append(parameter.toString());
            }
            builder.append(')');
        }
        final Data data = this.getData();
        if (data != null) {
            builder.append('<').append(data.toString()).append('>');
        }
        return builder.toString();
    }
}
