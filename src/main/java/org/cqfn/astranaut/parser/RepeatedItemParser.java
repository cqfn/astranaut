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

import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternMatchingMode;

/**
 * Parses a sequence of tokens as a repeated item.
 * @since 1.0.0
 */
final class RepeatedItemParser extends LeftSideItemParser {
    /**
     * Static instance.
     */
    public static final LeftSideItemParser INSTANCE = new RepeatedItemParser();

    /**
     * Private constructor.
     */
    private RepeatedItemParser() {
    }

    @Override
    public LeftSideItem parse(final LeftSideParser context, final Token first)
        throws ParsingException {
        final LeftSideItem item = context.parseLeftSideItem();
        if (item.getMatchingMode() != PatternMatchingMode.NORMAL) {
            throw new CommonParsingException(
                context.getLocation(),
                "Item can't be 'double repeated' or 'optional and repeated'"
            );
        }
        item.setMatchingMode(PatternMatchingMode.REPEATED);
        final Token next = context.getToken();
        if (!(next instanceof ClosingCurlyBracket)) {
            throw new CommonParsingException(
                context.getLocation(),
                "Missing curly closing bracket '}'"
            );
        }
        return item;
    }
}
