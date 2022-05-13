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
package org.uast.astgen.rules;

import java.util.Objects;

/**
 * Line of DSL code contains rule and addition data.
 * @param <T> Type of rule
 * @since 1.0
 */
public class Statement<T extends Rule> {
    /**
     * The rule.
     */
    private final T rule;

    /**
     * The programming language for which the rule is applied.
     */
    private final String language;

    /**
     * Constructor.
     * @param rule The rule
     * @param language The programming language
     */
    public Statement(final T rule, final String language) {
        this.rule = Objects.requireNonNull(rule);
        this.language = Objects.requireNonNull(language);
    }

    @Override
    public final String toString() {
        final String prefix;
        if (this.language.isEmpty()) {
            prefix = "green";
        } else {
            prefix = this.language;
        }
        return new StringBuilder()
            .append(prefix)
            .append(": ")
            .append(this.rule.toString())
            .toString();
    }

    /**
     * Creates base (untyped) statement.
     * @return Base statement
     */
    public Statement<Rule> toRuleStmt() {
        return new Statement<Rule>(this.rule, this.language);
    }

    /**
     * Returns the rule.
     * @return The rule
     */
    public T getRule() {
        return this.rule;
    }

    /**
     * Returns the programming language for which the rule is applied.
     * @return The language name
     */
    public String getLanguage() {
        return this.language;
    }
}
