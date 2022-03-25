/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Descriptor, i.e. name with tag, parameters and data.
 * Example: tag@Name(parameter)
 *
 * @since 1.0
 */
public class Descriptor {
    /**
     * Tag.
     */
    private final String tag;

    /**
     * Name.
     */
    private final String name;

    /**
     * List of parameters.
     */
    private final List<Descriptor> parameters;

    /**
     * Constructor.
     * @param tag Tag
     * @param name Name
     * @param parameters List of parameters
     */
    public Descriptor(final String tag, final String name,
        final List<Descriptor> parameters) {
        this.tag = Objects.requireNonNull(tag);
        this.name = Objects.requireNonNull(name);
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        if (!this.tag.isEmpty()) {
            builder.append(this.tag).append('@');
        }
        builder.append(this.name);
        if (!this.parameters.isEmpty()) {
            boolean flag = false;
            builder.append('(');
            for (final Descriptor parameter : this.parameters) {
                if (flag) {
                    builder.append(", ");
                }
                flag = true;
                builder.append(parameter.toString());
            }
            builder.append(')');
        }
        return builder.toString();
    }
}
