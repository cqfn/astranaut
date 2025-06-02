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
package org.cqfn.astranaut.dsl;

import java.util.List;
import org.cqfn.astranaut.codegen.java.AndExpressionMatcherGenerator;
import org.cqfn.astranaut.codegen.java.LeftSideItemGenerator;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Node;

/**
 * The 'AND' descriptor that combines multiple patterns and matches if all patterns match.
 * @since 1.0.0
 */
public final class AndExpression extends LogicalExpression {
    /**
     * Constructor.
     * @param items List of patterns that are combined in this expression
     */
    public AndExpression(final List<LeftSideItem> items) {
        super(items);
    }

    @Override
    public LeftSideItemGenerator createGenerator() {
        return new AndExpressionMatcherGenerator(this);
    }

    @Override
    public boolean matchNode(final Node node, final Extracted extracted) {
        boolean matches = true;
        for (final LeftSideItem item : this.getItems()) {
            if (!item.matchNode(node, extracted)) {
                matches = false;
                break;
            }
        }
        return matches;
    }

    @Override
    public char getSymbol() {
        return '&';
    }
}
