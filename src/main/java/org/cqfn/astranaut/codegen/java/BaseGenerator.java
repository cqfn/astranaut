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
            pkg = String.format("%s.%s", root, language.toLowerCase(Locale.ENGLISH));
        }
        return pkg;
    }
}
