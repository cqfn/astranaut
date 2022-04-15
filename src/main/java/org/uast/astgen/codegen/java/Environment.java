/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;

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
     * Returns version of the implementation.
     * @return The version
     */
    String getVersion();

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

    /**
     * Returns node hierarchy (waiting for PWC).
     * @param name Node name
     * @return Node hierarchy
     */
    List<String> getHierarchy(String name);
}
