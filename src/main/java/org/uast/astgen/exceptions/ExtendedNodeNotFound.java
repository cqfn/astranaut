/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.exceptions;

/**
 * Exception "Extended vertex was not found in green DSL rules".
 *
 * @since 1.0
 */
public final class ExtendedNodeNotFound extends GeneratorException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text.
     */
    public ExtendedNodeNotFound(final String text) {
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return String.format("The extended node '%s' was not found in green DSL rules", this.text);
    }
}
