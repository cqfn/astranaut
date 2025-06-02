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

import java.util.Collections;
import java.util.List;

/**
 * A descriptor of a logical expression that combines multiple patterns.
 * @since 1.0.0
 */
public abstract class LogicalExpression implements LeftSideItem, PatternItem {
    /**
     * The matching compatibility of the typed hole descriptor.
     */
    private PatternMatchingMode mode;

    /**
     * Negation flag.
     */
    private boolean negation;

    /**
     * List of patterns that are combined in this expression.
     */
    private final List<LeftSideItem> items;

    /**
     * Constructor.
     * @param items List of patterns that are combined in this expression
     */
    public LogicalExpression(final List<LeftSideItem> items) {
        this.items = LogicalExpression.checkItemsList(items);
        this.mode = PatternMatchingMode.NORMAL;
    }

    @Override
    public final void setMatchingMode(final PatternMatchingMode value) {
        this.mode = value;
    }

    @Override
    public final PatternMatchingMode getMatchingMode() {
        return this.mode;
    }

    @Override
    public final void setNegationFlag() {
        this.negation = true;
    }

    @Override
    public final boolean isNegationFlagSet() {
        return this.negation;
    }

    @Override
    public final String toString() {
        return this.toFullString();
    }

    @Override
    public final String toString(final boolean full) {
        final String result;
        if (full) {
            result = this.toFullString();
        } else {
            result = this.toShortString();
        }
        return result;
    }

    /**
     * Returns the symbol of the logical expression.
     * @return Symbol (& or |)
     */
    public abstract char getSymbol();

    /**
     * Returns list of patterns that are combined in this expression.
     * @return List of patterns
     */
    public List<LeftSideItem> getItems() {
        return this.items;
    }

    /**
     * Represents the descriptor as a string in short form, without matching mode.
     * @return Short pattern descriptor as a string
     */
    private String toShortString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getSymbol()).append('(');
        boolean flag = false;
        for (final LeftSideItem item : this.items) {
            if (flag) {
                builder.append(", ");
            }
            builder.append(item.toString(false));
            flag = true;
        }
        builder.append(')');
        return builder.toString();
    }

    /**
     * Represents the descriptor as a string in full form, with matching mode.
     * @return Full pattern descriptor as a string
     */
    private String toFullString() {
        final StringBuilder builder = new StringBuilder();
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append('[');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('{');
        }
        builder.append(this.toShortString());
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append(']');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('}');
        }
        return builder.toString();
    }

    /**
     * Checks the list of items. It must be non-empty.
     * @param items List of patterns that are combined in this expression
     * @return Verified non-mutable list
     */
    private static List<LeftSideItem> checkItemsList(final List<LeftSideItem> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return Collections.unmodifiableList(items);
    }
}
