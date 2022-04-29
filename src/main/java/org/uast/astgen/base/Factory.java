/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.base;

import java.util.Map;

/**
 * The node factory.
 *
 * @since 1.0
 */
public class Factory {
    /**
     * The set of types arranged by name.
     */
    private final Map<String, Type> types;

    /**
     * Constructor.
     * @param types The set of types arranged by name
     */
    public Factory(final Map<String, Type> types) {
        this.types = types;
    }

    /**
     * Creates node builder by type name.
     * @param name The type name
     * @return A node builder
     */
    public final Builder createBuilder(final String name) {
        final Builder result;
        if (this.types.containsKey(name)) {
            final Type type = this.types.get(name);
            result = type.createBuilder();
        } else {
            final DraftNode.Constructor draft = new DraftNode.Constructor();
            draft.setName(name);
            result = draft;
        }
        return result;
    }
}
