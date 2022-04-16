/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Locale;

/**
 * Generates source code for rules.
 *
 * @since 1.0
 */
abstract class BaseGenerator {
    /**
     * The environment.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param env The environment required for generation.
     */
    BaseGenerator(final Environment env) {
        this.env = env;
    }

    /**
     * Generates Java compilation unit that contains source code that implements a rule.
     * @return Java compilation unit
     */
    public abstract CompilationUnit generate();

    /**
     * Returns the environment.
     * @return The environment
     */
    protected Environment getEnv() {
        return this.env;
    }

    /**
     * Returns full package name.
     * @param language The programming language for which the rule is applied
     * @return Package name
     */
    protected String getPackageName(final String language) {
        final String root = this.env.getRootPackage();
        final String pkg;
        if (language.isEmpty()) {
            pkg = root.concat(".green");
        } else {
            pkg = root.concat(language.toLowerCase(Locale.ENGLISH));
        }
        return pkg;
    }
}
