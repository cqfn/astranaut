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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.rules.Statement;
import org.cqfn.astranaut.rules.Transformation;

/**
 * Generates source code for transformation rules.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public class TransformationGenerator {
    /**
     * The environment.
     */
    private final Environment env;

    /**
     * List of rules.
     */
    private final List<Statement<Transformation>> rules;

    /**
     * Processing language.
     */
    private final String language;

    /**
     * The collection of compilation units.
     */
    private final Map<String, CompilationUnit> units;

    /**
     * Constructor.
     * @param env The environment
     * @param rules The list of rules
     * @param language The processing language
     */
    public TransformationGenerator(final Environment env,
        final List<Statement<Transformation>> rules,
        final String language) {
        this.env = env;
        this.rules = rules;
        this.language = language;
        this.units = new TreeMap<>();
    }

    /**
     * Generates source code for transformation rules.
     */
    public void generate() {
        final String root = this.env.getRootPackage();
        final String pkg = String.format(
            "%s.%s.rules",
            root,
            this.language.toLowerCase(Locale.ENGLISH)
        );
        final MatcherGenerator matchers = new MatcherGenerator(this.env, pkg);
        final ConverterGenerator converters = new ConverterGenerator(this.env, pkg);
        int count = 0;
        for (final Statement<Transformation> stmt : this.rules) {
            if (stmt.getLanguage().equals(this.language)) {
                final Transformation rule = stmt.getRule();
                final String matcher = matchers.generate(rule.getLeft());
                converters.generate(rule.getRight(), matcher);
                count = count + 1;
            }
        }
        this.units.putAll(matchers.getUnits());
        this.units.putAll(converters.getUnits());
        final AdapterGenerator adapter = new AdapterGenerator(this.env, this.language, count);
        final CompilationUnit unit = adapter.generate();
        this.units.put(adapter.getClassname(), unit);
    }

    /**
     * Returns the collection of generated units.
     * @return Generated units
     */
    public Map<String, CompilationUnit> getUnits() {
        return Collections.unmodifiableMap(this.units);
    }
}
