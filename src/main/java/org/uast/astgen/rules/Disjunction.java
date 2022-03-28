/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.Collections;
import java.util.List;

/**
 * Disjunction, i.e. logical OR expression.
 *
 * @since 1.0
 */
public class Disjunction implements Child {
    /**
     * List of descriptors.
     */
    private final List<Descriptor> descriptors;

    /**
     * Constructor.
     *
     * @param descriptors List of descriptors
     */
    public Disjunction(final List<Descriptor> descriptors) {
        this.descriptors = Collections.unmodifiableList(descriptors);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        boolean flag = false;
        for (final Descriptor descriptor : this.descriptors) {
            if (flag) {
                builder.append(" | ");
            }
            builder.append(descriptor.toString());
            flag = true;
        }
        return builder.toString();
    }

    /**
     * Returns the list of descriptors.
     * @return List of descriptors
     */
    public List<Descriptor> getDescriptors() {
        return this.descriptors;
    }
}
