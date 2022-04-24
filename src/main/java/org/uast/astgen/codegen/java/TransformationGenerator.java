/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

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
    }

    /**
     * Generates source code for transformation rules.
     * @return The collection of compilation units
     */
    public Map<String, CompilationUnit> generate() {
        final Map<String, CompilationUnit> units = new TreeMap<>();
        final String pkg = this.getPackageName();
        final MatcherGenerator matchers = new MatcherGenerator(this.env, pkg);
        final ConverterGenerator converters = new ConverterGenerator(this.env, pkg);
        for (final Statement<Transformation> stmt : this.rules) {
            if (stmt.getLanguage().equals(this.language)) {
                final Transformation rule = stmt.getRule();
                final String matcher = matchers.generate(rule.getLeft());
                converters.generate(rule.getRight(), matcher);
            }
        }
        units.putAll(matchers.getUnits());
        units.putAll(converters.getUnits());
        return units;
    }

    /**
     * Returns full package name.
     * @return Package name
     */
    protected String getPackageName() {
        final String root = this.env.getRootPackage();
        return String.format("%s.%s.rules", root, this.language.toLowerCase(Locale.ENGLISH));
    }
}
