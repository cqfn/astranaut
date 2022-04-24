/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.rules.Transformation;

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
