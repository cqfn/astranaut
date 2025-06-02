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
package org.cqfn.astranaut.parser;

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.OrExpression;
import org.cqfn.astranaut.dsl.PatternMatchingMode;

/**
 * Parses a sequence of tokens as OR expression.
 * @since 1.0.0
 */
final class OrExpressionParser extends LeftSideItemParser {
    /**
     * Static instance.
     */
    public static final LeftSideItemParser INSTANCE = new OrExpressionParser();

    /**
     * Private constructor.
     */
    private OrExpressionParser() {
    }

    @Override
    public LeftSideItem parse(final LeftSideParser context, final Token first)
        throws ParsingException {
        Token next = context.getToken();
        if (!(next instanceof OpeningRoundBracket)) {
            throw new CommonParsingException(
                context.getLocation(),
                "Expected '(' after '|'"
            );
        }
        final List<LeftSideItem> items = new ArrayList<>(1);
        context.incrementNestingLevel();
        while (true) {
            final LeftSideItem item = context.parseLeftSideItem();
            if (item == null) {
                throw new CommonParsingException(
                    context.getLocation(),
                    "Expected an item inside '|(' and ')'"
                );
            }
            if (item.getMatchingMode() != PatternMatchingMode.NORMAL) {
                throw new CommonParsingException(
                    context.getLocation(),
                    "Elements within an OR expression cannot be optional or repetitive"
                );
            }
            items.add(item);
            next = context.getToken();
            if (next instanceof ClosingRoundBracket) {
                break;
            }
            if (next instanceof Comma) {
                continue;
            }
            throw new CommonParsingException(
                context.getLocation(),
                "OR expression must be separated by commas and ends with ')'"
            );
        }
        context.decrementNestingLevel();
        return new OrExpression(items);
    }
}
