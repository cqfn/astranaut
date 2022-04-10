/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

/**
 * Some entity of the Java language.
 *
 * @since 1.0
 */
public interface Entity {
    /**
     * The tabulation size, i.e. number of repeated spaces.
     */
    int TAB_SIZE = 4;

    /**
     * Generates source code.
     * @param indent Indentation from the beginning of the line
     * @return Java source code
     */
    String generate(int indent);
}
