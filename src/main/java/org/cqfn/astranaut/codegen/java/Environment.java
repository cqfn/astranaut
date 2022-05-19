/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.codegen.java;

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
