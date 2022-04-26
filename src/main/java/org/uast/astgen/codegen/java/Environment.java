/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import java.util.Set;

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
     * Returns the "test" flag.
     * The flag means that no files will be written to the file system. The program
     * will only check the structure of the DSL and the possibility of generating files.
     * @return The flag
     */
    boolean isTestMode();

    /**
     * Returns the language for which this environment was built.
     * @return The language
     */
    String getLanguage();

    /**
     * Returns node hierarchy.
     * @param type Node type
     * @return Node hierarchy
     */
    List<String> getHierarchy(String type);

    /**
     * Return a list of tagged children by type name.
     * @param type Node type
     * @return List of children
     */
    List<TaggedChild> getTags(String type);

    /**
     * The list of node types that should be added to an import block
     *  of the specified node.
     * @param type Node type
     * @return The list of type names
     */
    Set<String> getImports(String type);
}
