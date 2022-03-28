/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
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
}
