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

import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.OrExpression;

/**
 * Generates a matcher class for matching OR expression.
 * @since 1.0.0
 */
public final class OrExpressionMatcherGenerator extends LeftSideItemGenerator {
    /**
     * Expression for which the matcher is generated.
     */
    private final OrExpression expression;

    /**
     * Constructor.
     * @param expression Expression for which the matcher is generated
     */
    public OrExpressionMatcherGenerator(final OrExpression expression) {
        this.expression = expression;
    }

    @Override
    public Klass generate(final LeftSideGenerationContext context) {
        final String brief = String.format(
            "Matches a node with the pattern '%s' and extracts it if matched",
            this.expression.toString(false)
        );
        final Klass klass = new Klass(context.generateClassName(), brief);
        LeftSideItemGenerator.generateInstanceAndConstructor(klass);
        this.generateMatchMethod(context, klass);
        return klass;
    }

    /**
     * Generates and adds a {@code match} method to the given class.
     * @param context Generation context
     * @param klass The class to which the {@code match} method will be added
     */
    private void generateMatchMethod(final LeftSideGenerationContext context, final Klass klass) {
        final Method method = new Method("boolean", "match");
        klass.addMethod(method);
        method.makePublic();
        method.addArgument("Node", "node");
        method.addArgument("Extracted", "extracted");
        final StringBuilder builder = new StringBuilder(64);
        builder.append("return ");
        if (this.expression.isNegationFlagSet()) {
            builder.append("!(");
        }
        boolean flag = false;
        for (final LeftSideItem item : this.expression.getItems()) {
            if (flag) {
                builder.append(" || ");
            }
            flag = true;
            final Klass matcher = item.generateMatcher(context);
            builder.append(matcher.getName()).append(".INSTANCE.match(node, extracted)");
        }
        if (this.expression.isNegationFlagSet()) {
            builder.append(')');
        }
        builder.append(';');
        method.setBody(builder.toString());
    }
}
