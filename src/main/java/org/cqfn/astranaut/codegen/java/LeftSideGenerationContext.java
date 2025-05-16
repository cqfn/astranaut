/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * All that is needed to generate a set of matchers from a set of rules.
 * @since 1.0.0
 */
public final class LeftSideGenerationContext {
    /**
     * Map storing generated matcher classes.
     */
    private final Map<String, Klass> matchers;

    /**
     * Generator for sequential labels used in class naming.
     */
    private final NumberedLabelGenerator labels;

    /**
     * List of additional imported libraries for each generated class.
     */
    private final Map<Klass, Set<String>> imports;

    /**
     * Constructor.
     */
    public LeftSideGenerationContext() {
        this.matchers = new TreeMap<>();
        this.labels = new NumberedLabelGenerator("Matcher");
        this.imports = new HashMap<>();
    }

    /**
     * Returns a collection of generated matcher classes correlated with [sub]rules in text form.
     * @return Map storing generated matcher classes
     */
    public Map<String, Klass> getMatchers() {
        return this.matchers;
    }

    /**
     * Generates a class name for a new matcher.
     * @return Unique class name
     */
    public String generateClassName() {
        return this.labels.getLabel();
    }

    /**
     * Adds the imported library to the class.
     * @param klass Class
     * @param name Full library name (with package)
     */
    public void addImport(final Klass klass, final String name) {
        final Set<String> set = this.imports.computeIfAbsent(klass, x -> new TreeSet<>());
        set.add(name);
    }

    /**
     * Returns set of imported names for generated class.
     * @param klass Class
     * @return Set of names (can be empty)
     */
    public Set<String> getImports(final Klass klass) {
        return this.imports.getOrDefault(klass, Collections.emptySet());
    }
}
