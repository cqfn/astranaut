/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

/**
 * Environment required for generation.
 *
 * @since 1.0
 */
public interface Environment {
    /**
     * Returns license generator.
     * @return The license
     */
    License getLicense();

    /**
     * Returns the name of the package being created.
     * @return The package name
     */
    String getRootPackage();

    /**
     * Returns the name of the package that contains the 'Node' base interface.
     * @return The package name
     */
    String getBasePackage();
}
