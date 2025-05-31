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

import java.util.Map;
import org.cqfn.astranaut.core.utils.MapUtils;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternItem;

/**
 * Parses an item that is part of the left side of a transformation rule.
 * @since 1.0.0
 */
final class LeftSideParsing {
    /**
     * Mapping token classes and parsers that fit them.
     */
    private static final Map<Class<? extends Token>, LeftSideItemParser> PARSERS =
        new MapUtils<Class<? extends Token>, LeftSideItemParser>()
            .put(SymbolicToken.class, SymbolicDescriptorParser.INSTANCE)
            .make();

    /**
     * Data that is required during parsing.
     */
    private final LeftSideParsingContext context;

    /**
     * Constructor.
     * @param context Data that is required during parsing
     */
    LeftSideParsing(final LeftSideParsingContext context) {
        this.context = context;
    }

    /**
     * Parses an item that is part of the left side of a transformation rule.
     * @return Left item, that is, the pattern descriptor or typed hole; or {@code null} if
     *  the scanner does not contain any more tokens
     * @throws ParsingException If the parse fails
     */
    LeftSideItem parseLeftSideItem() throws ParsingException {
        final Token first = this.context.getToken();
        return this.parseLeftSideItem(first);
    }

    /**
     * Parses an item that is part of the left side of a transformation rule, provided that
     *  the first token is known.
     * @param first The first token in the token sequence
     * @return Left item, that is, the pattern descriptor or typed hole; or {@code null} if
     *  the scanner does not contain any more tokens
     * @throws ParsingException If the parse fails
     */
    LeftSideItem parseLeftSideItem(final Token first) throws ParsingException {
        final LeftSideItem item;
        do {
            if (first == null) {
                item = null;
                break;
            }
            final LeftSideItemParser parser = LeftSideParsing.PARSERS.get(first.getClass());
            if (parser != null) {
                item = parser.parse(this.context, first);
                break;
            }
            if (first instanceof HashSymbol) {
                throw new CommonParsingException(
                    this.context.getLocation(),
                    "The left part of the transformation descriptor cannot contain untyped holes"
                );
            } else {
                throw new InappropriateToken(this.context.getLocation(), first);
            }
        } while (false);
        return item;
    }

    /**
     * Parses a child element of the pattern, which can either be a pattern descriptor or a hole.
     * @return Pattern item, that is, the pattern descriptor or hole; or {@code null} if
     *  the scanner does not contain any more tokens
     * @throws ParsingException If the parse fails
     */
    PatternItem parsePatternItem() throws ParsingException {
        final PatternItem item;
        final Token first = this.context.getToken();
        do {
            if (first == null
                || first instanceof ClosingRoundBracket && this.context.getNestingLevel() > 0) {
                item = null;
                break;
            }
            final LeftSideItemParser parser = LeftSideParsing.PARSERS.get(first.getClass());
            if (parser != null) {
                item = (PatternItem) parser.parse(this.context, first);
                break;
            }
            if (first instanceof HashSymbol) {
                item = LeftSideItemParser.parseUntypedNodeHole(this.context);
                break;
            } else {
                throw new InappropriateToken(this.context.getLocation(), first);
            }
        } while (false);
        return item;
    }
}
