/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

/**
 * Generates unique class names.
 *
 * @since 1.0
 */
public class ClassNameGenerator {
    /**
     * Unique number.
     */
    private int number;

    /**
     * Name prefix.
     */
    private final String prefix;

    /**
     * Constructor.
     * @param prefix Name prefix
     */
    public ClassNameGenerator(final String prefix) {
        this.number = -1;
        this.prefix = prefix;
    }

    /**
     * Retuns unique class name.
     * @return Class name
     */
    public String getName() {
        this.number = this.number + 1;
        return String.format("%s%d", this.prefix, this.number);
    }
}
