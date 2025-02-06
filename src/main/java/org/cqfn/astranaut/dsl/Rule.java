/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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
package org.cqfn.astranaut.dsl;

import java.util.Set;
import org.cqfn.astranaut.codegen.java.RuleGenerator;

/**
 * One rule of the DSL language. Describes either a node or a transformation.
 * @since 1.0.0
 */
public interface Rule {
    /**
     * Returns the name of the programming language for which this rule is described.
     * If no language is defined, returns the string "common".
     * @return The name of the programming language, never null or empty.
     */
    String getLanguage();

    @Override
    String toString();

    /**
     * Adds a node descriptor as a dependency.
     * @param descriptor The node descriptor to add
     */
    void addDependency(NodeDescriptor descriptor);

    /**
     * Returns the set of dependencies for this rule.
     * @return Set of node descriptors
     */
    Set<NodeDescriptor> getDependencies();

    /**
     * Creates a suitable generator that generates Java code.
     * @return Generator
     */
    RuleGenerator createGenerator();
}
