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
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Parses some item of the left side of the transformation rule using some algorithm.
 * @since 1.0.0
 */
abstract class LeftSideItemParser {
    /**
     * Parses some item of the left side of the transformation rule using some algorithm.
     * @param context Left side parser as a parsing context
     * @param first First token already obtained from the scanner
     *  and suitable for this type of parser
     * @return Resulting item
     * @throws ParsingException If parsing fails
     */
    public abstract LeftSideItem parse(LeftSideParser context, Token first)
        throws ParsingException;

    /**
     * Parses a sequence of tokens as an [untyped] data hole.
     * @param context Data that is required during parsing
     * @return An untyped hole
     * @throws ParsingException If the parse fails
     */
    protected static UntypedHole parseDataHole(final LeftSideParser context)
        throws ParsingException {
        final Token token = context.getToken();
        if (!(token instanceof Number)) {
            throw new BadHole(context.getLocation());
        }
        final int value = ((Number) token).getValue();
        context.getHoleCounter().addDataHole(value);
        return UntypedHole.getInstance(value);
    }

    /**
     * Parses a sequence of tokens as an untyped node hole.
     * @param context Data that is required during parsing
     * @return An untyped hole
     * @throws ParsingException If the parse fails
     */
    protected static UntypedHole parseUntypedNodeHole(final LeftSideParser context)
        throws ParsingException {
        final Token token = context.getToken();
        if (!(token instanceof Number)) {
            throw new BadHole(context.getLocation());
        }
        final int value = ((Number) token).getValue();
        final HoleCounter holes = context.getHoleCounter();
        if (holes.hasNodeHole(value)) {
            throw new CommonParsingException(
                context.getLocation(),
                String.format("Hole with number #%d replacing a node has already been used", value)
            );
        }
        holes.addNodeHole(value);
        return UntypedHole.getInstance(value);
    }
}
