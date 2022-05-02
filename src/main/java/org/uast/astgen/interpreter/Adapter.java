/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.rules.Transformation;

/**
 * Tree converter built on a set of rules described in DSL.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public class Adapter extends org.uast.astgen.base.Adapter {
    /**
     * Constructor.
     * @param statements The list of transformation statements
     */
    public Adapter(final List<Statement<Transformation>> statements) {
        super(Collections.unmodifiableList(Adapter.init(statements)), Factory.INSTANCE);
    }

    /**
     * Initialises the list of converters.
     * @param statements The list of transformation statements
     * @return List of converters
     */
    private static List<org.uast.astgen.base.Converter> init(
        final List<Statement<Transformation>> statements) {
        final List<org.uast.astgen.base.Converter> result = new ArrayList<>(statements.size());
        for (final Statement<Transformation> statement : statements) {
            final Transformation rule = statement.getRule();
            result.add(new Converter(rule));
        }
        return result;
    }
}
