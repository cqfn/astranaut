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
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.dsl.LeftDataDescriptor;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternDescriptor;
import org.cqfn.astranaut.dsl.PatternItem;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.TypedHole;

/**
 * Parses a sequence of tokens as a pattern descriptor or typed hole.
 * @since 1.0.0
 */
final class PatternParser extends LeftSideItemParser {
    /**
     * Static instance.
     */
    public static final LeftSideItemParser INSTANCE = new PatternParser();

    /**
     * Private constructor.
     */
    private PatternParser() {
    }

    @Override
    public LeftSideItem parse(final LeftSideParser context, final Token first)
        throws ParsingException {
        final String type = first.toString();
        final Token next = context.getToken();
        final LeftSideItem result;
        if (next instanceof HashSymbol) {
            result = PatternParser.parseTypedHole(context, type);
        } else {
            result = PatternParser.parsePattern(context, type, next);
        }
        return result;
    }

    /**
     * Parses a sequence of tokens as a typed hole.
     * @param context Left side parser as a parsing context
     * @param type Type of the hole
     * @return A typed hole
     * @throws ParsingException If the parse fails
     */
    private static TypedHole parseTypedHole(final LeftSideParser context, final String type)
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
        return new TypedHole(type, value);
    }

    /**
     * Parses a sequence of tokens as a pattern descriptor.
     * @param context Left side parser as a parsing context
     * @param type The type of the root node of the subtree
     * @param first First token
     * @return Subtree descriptor
     * @throws ParsingException If the parse fails
     */
    private static PatternDescriptor parsePattern(final LeftSideParser context, final String type,
        final Token first) throws ParsingException {
        Token next = first;
        LeftDataDescriptor data = null;
        if (next instanceof OpeningAngleBracket) {
            data = PatternParser.parseData(context);
            next = context.getToken();
        }
        List<PatternItem> children = Collections.emptyList();
        if (next instanceof OpeningRoundBracket) {
            children = PatternParser.parseChildren(context);
            next = context.getToken();
        }
        if (next instanceof ClosingRoundBracket && context.getNestingLevel() == 0) {
            throw new CommonParsingException(
                context.getLocation(),
                "Unmatched closing parenthesis ')' found"
            );
        }
        if (next == null && context.getNestingLevel() > 0) {
            throw new CommonParsingException(
                context.getLocation(),
                "Unmatched opening parenthesis '(' found"
            );
        }
        context.pushToken(next);
        return new PatternDescriptor(type, data, children);
    }

    /**
     * Parses a sequence of tokens as a data descriptor.
     * @param context Left side parser as a parsing context
     * @return A data descriptor
     * @throws ParsingException If the parse fails
     */
    private static LeftDataDescriptor parseData(final LeftSideParser context)
        throws ParsingException {
        final LeftDataDescriptor data;
        final Token first = context.getToken();
        if (first instanceof HashSymbol) {
            data = LeftSideItemParser.parseDataHole(context);
        } else if (first instanceof CharSequenceToken) {
            data = new StaticString((CharSequenceToken) first);
        } else {
            throw new CommonParsingException(
                context.getLocation(),
                "Invalid data inside data descriptor. Expected either a hole ('#...') or a string"
            );
        }
        final Token next = context.getToken();
        if (!(next instanceof ClosingAngleBracket)) {
            throw new CommonParsingException(
                context.getLocation(),
                "Closing angle bracket '>' expected for data descriptor"
            );
        }
        return data;
    }

    /**
     * Parses a sequence of tokens as a list of pattern children.
     * @param context Left side parser as a parsing context
     * @return List of pattern children
     * @throws ParsingException If the parse fails
     */
    private static List<PatternItem> parseChildren(final LeftSideParser context)
        throws ParsingException {
        final List<PatternItem> list = new ArrayList<>(0);
        do {
            context.incrementNestingLevel();
            final PatternItem child = context.parsePatternItem();
            context.decrementNestingLevel();
            if (child == null) {
                break;
            }
            list.add(child);
            final Token next = context.getToken();
            if (next instanceof ClosingRoundBracket) {
                break;
            }
            if (!(next instanceof Comma)) {
                throw new CommonParsingException(
                    context.getLocation(),
                    "Child descriptors must be separated by commas"
                );
            }
        } while (true);
        return list;
    }
}
