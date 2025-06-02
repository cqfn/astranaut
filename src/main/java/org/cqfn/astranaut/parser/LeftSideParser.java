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
import java.util.Stack;
import org.cqfn.astranaut.core.utils.MapUtils;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternItem;

/**
 * Parses an item that is part of the left side of a transformation rule.
 * @since 1.0.0
 */
final class LeftSideParser {
    /**
     * Mapping token classes and parsers that fit them.
     */
    private static final Map<Class<? extends Token>, LeftSideItemParser> PARSERS =
        new MapUtils<Class<? extends Token>, LeftSideItemParser>()
            .put(SymbolRangeToken.class, SymbolicDescriptorParser.INSTANCE)
            .put(SymbolToken.class, SymbolicDescriptorParser.INSTANCE)
            .put(OpeningSquareBracket.class, OptionalItemParser.INSTANCE)
            .put(OpeningCurlyBracket.class, RepeatedItemParser.INSTANCE)
            .put(Identifier.class, PatternParser.INSTANCE)
            .put(Tilde.class, NegativeItemParser.INSTANCE)
            .put(VerticalLine.class, OrExpressionParser.INSTANCE)
            .put(Ampersand.class, AndExpressionParser.INSTANCE)
            .make();

    /**
     * Scanner that issues tokens.
     */
    private final Scanner scanner;

    /**
     * Tokens that have been received from the scanner but not used.
     */
    private final Stack<Token> tokens;

    /**
     * Pattern nesting level.
     */
    private int nesting;

    /**
     * Counter for tracking placeholders (holes).
     */
    private final HoleCounter holes;

    /**
     * Constructor.
     * @param scanner Scanner that issues tokens
     * @param holes Counter for tracking placeholders (holes)
     */
    LeftSideParser(final Scanner scanner, final HoleCounter holes) {
        this.scanner = scanner;
        this.tokens = new Stack<>();
        this.nesting = 0;
        this.holes = holes;
    }

    /**
     * Returns the next token.
     * @return A token or {@code null} if no more tokens
     * @throws ParsingException If the scanner fails
     */
    Token getToken() throws ParsingException {
        final Token token;
        if (this.tokens.empty()) {
            token = this.scanner.getToken();
        } else {
            token = this.tokens.pop();
        }
        return token;
    }

    /**
     * Returns location of the DSL code.
     * @return Location of the DSL code
     */
    Location getLocation() {
        return this.scanner.getLocation();
    }

    /**
     * Saves an unused token so that another parser can use it.
     * @param token A token
     */
    void pushToken(final Token token) {
        if (token != null) {
            this.tokens.push(token);
        }
    }

    /**
     * Returns the current nesting level of patterns.
     * @return Nesting level
     */
    int getNestingLevel() {
        return this.nesting;
    }

    /**
     * Increases the nesting level by 1.
     */
    void incrementNestingLevel() {
        this.nesting = this.nesting + 1;
    }

    /**
     * Decreases the nesting level by 1.
     */
    void decrementNestingLevel() {
        if (this.nesting == 0) {
            throw new UnsupportedOperationException();
        }
        this.nesting = this.nesting - 1;
    }

    /**
     * Returns the counter for tracking placeholders (holes).
     * @return Hole counter
     */
    HoleCounter getHoleCounter() {
        return this.holes;
    }

    /**
     * Parses an item that is part of the left side of a transformation rule.
     * @return Left item, that is, the pattern descriptor or typed hole; or {@code null} if
     *  the scanner does not contain any more tokens
     * @throws ParsingException If the parse fails
     */
    LeftSideItem parseLeftSideItem() throws ParsingException {
        final Token first = this.getToken();
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
            final LeftSideItemParser parser = LeftSideParser.PARSERS.get(first.getClass());
            if (parser != null) {
                item = parser.parse(this, first);
                break;
            }
            if (first instanceof HashSymbol) {
                throw new CommonParsingException(
                    this.getLocation(),
                    "The left part of the transformation descriptor, optional and repeated items cannot contain untyped holes"
                );
            } else {
                throw new InappropriateToken(this.getLocation(), first);
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
        final Token first = this.getToken();
        do {
            if (first == null
                || first instanceof ClosingRoundBracket && this.getNestingLevel() > 0) {
                item = null;
                break;
            }
            final LeftSideItemParser parser = LeftSideParser.PARSERS.get(first.getClass());
            if (parser != null) {
                item = (PatternItem) parser.parse(this, first);
                break;
            }
            if (first instanceof HashSymbol) {
                item = LeftSideItemParser.parseUntypedNodeHole(this);
                break;
            } else {
                throw new InappropriateToken(this.getLocation(), first);
            }
        } while (false);
        return item;
    }
}
