/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.exceptions;

/**
 * Exception "Duplicate vertex found in DSL rules".
 *
 * @since 1.0
 */
public final class DuplicateRule extends GeneratorException {
    /**
     * The explanatory text.
     */
    private final String text;

    /**
     * Constructor.
     * @param text The explanatory text.
     */
    public DuplicateRule(final String text) {
        this.text = text;
    }

    @Override
    public String getErrorMessage() {
        return String.format("Duplication in DSL rules: '%s'", this.text);
    }
}
