/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

/**
 * Source code in the Java programming language, fully assembled and ready to be saved.
 *
 * @since 1.0
 */
public interface JavaFile {
    /**
     * Specifies the class version.
     * @param version The version
     */
    void setVersion(String version);

    /**
     * Generates Java source code.
     * @return Source code
     */
    String generate();
}
