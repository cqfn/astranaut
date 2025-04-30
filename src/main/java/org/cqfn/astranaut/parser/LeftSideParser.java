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
import org.cqfn.astranaut.dsl.PatternMatchingMode;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.TypedHole;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Parses an item that is part of the left side of a transformation rule.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public class LeftSideParser {
    /**
     * Scanner that produces a sequence of tokens.
     */
    private final Scanner scanner;

    /**
     * Descriptor nesting level.
     */
    private final int nesting;

    /**
     * Count of holes resulting from parsing.
     */
    private final HoleCounter holes;

    /**
     * The last token received from the scanner but not accepted by the parser.
     */
    private Token last;

    /**
     * Constructor.
     *
     * @param scanner Scanner
     * @param nesting Descriptor nesting level
     * @param holes Count of holes resulting from parsing
     */
    public LeftSideParser(final Scanner scanner, final int nesting, final HoleCounter holes) {
        this.scanner = scanner;
        this.nesting = nesting;
        this.holes = holes;
    }

    /**
     * Parses an item that is part of the left side of a transformation rule.
     * @return Left item, that is, the pattern descriptor or typed hole; or {@code null} if
     *  the scanner does not contain any more tokens
     * @throws ParsingException If the parse fails
     */
    public LeftSideItem parseLeftSideItem() throws ParsingException {
        final Token first = this.scanner.getToken();
        final LeftSideItem item;
        if (first instanceof Identifier) {
            item = this.parsePatternOrTypedHole(first.toString());
        } else if (first instanceof OpeningSquareBracket) {
            item = this.parseOptionalItem();
        } else if (first instanceof OpeningCurlyBracket) {
            item = this.parseRepeatedItem();
        } else if (first == null) {
            item = null;
        } else if (first instanceof HashSymbol) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "The left part of the transformation descriptor cannot contain untyped holes"
            );
        } else {
            throw new InappropriateToken(this.scanner.getLocation(), first);
        }
        return item;
    }

    /**
     * Parses a child element of the pattern, which can either be a pattern descriptor or a hole.
     * @return Pattern item, that is, the pattern descriptor or hole; or {@code null} if
     *  the scanner does not contain any more tokens
     * @throws ParsingException If the parse fails
     */
    public PatternItem parsePatternItem() throws ParsingException {
        final Token first = this.scanner.getToken();
        final PatternItem item;
        if (first instanceof Identifier) {
            item = (PatternItem) this.parsePatternOrTypedHole(first.toString());
        } else if (first instanceof OpeningSquareBracket) {
            item = (PatternItem) this.parseOptionalItem();
        } else if (first instanceof OpeningCurlyBracket) {
            item = (PatternItem) this.parseRepeatedItem();
        } else if (first == null || first instanceof ClosingRoundBracket && this.nesting > 0) {
            item = null;
        } else if (first instanceof HashSymbol) {
            item = this.parseUntypedNodeHole();
        } else {
            throw new InappropriateToken(this.scanner.getLocation(), first);
        }
        return item;
    }

    /**
     * Return the last token received from the scanner but not accepted by the parser.
     * @return Last token or {@code null} if all tokens have been accepted.
     */
    public Token getLastToken() {
        return this.last;
    }

    /**
     * Parses a sequence of tokens as an untyped node hole.
     * @return An untyped hole
     * @throws ParsingException If the parse fails
     */
    private UntypedHole parseUntypedNodeHole() throws ParsingException {
        final Token token = this.scanner.getToken();
        if (!(token instanceof Number)) {
            throw new BadHole(this.scanner.getLocation());
        }
        final int value = ((Number) token).getValue();
        if (this.holes.hasNodeHole(value)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                String.format("Hole with number #%d replacing a node has already been used", value)
            );
        }
        this.holes.addNodeHole(value);
        return UntypedHole.getInstance(value);
    }

    /**
     * Parses a sequence of tokens as an untyped data hole.
     * @return An untyped hole
     * @throws ParsingException If the parse fails
     */
    private UntypedHole parseUntypedDataHole() throws ParsingException {
        final Token token = this.scanner.getToken();
        if (!(token instanceof Number)) {
            throw new BadHole(this.scanner.getLocation());
        }
        final int value = ((Number) token).getValue();
        if (this.holes.hasDataHole(value)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                String.format("Hole with number #%d replacing data has already been used", value)
            );
        }
        this.holes.addDataHole(value);
        return UntypedHole.getInstance(value);
    }

    /**
     * Parses a sequence of tokens as a typed hole.
     * @param type Type of the hole
     * @return A typed hole
     * @throws ParsingException If the parse fails
     */
    private TypedHole parseTypedHole(final String type) throws ParsingException {
        final Token token = this.scanner.getToken();
        if (!(token instanceof Number)) {
            throw new BadHole(this.scanner.getLocation());
        }
        final int value = ((Number) token).getValue();
        if (this.holes.hasNodeHole(value)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                String.format("Hole with number #%d replacing a node has already been used", value)
            );
        }
        this.holes.addNodeHole(value);
        return new TypedHole(type, value);
    }

    /**
     * Parses a sequence of tokens as a pattern descriptor or typed hole.
     * @param type The type of the root node of the subtree
     * @return Pattern descriptor or typed hole
     * @throws ParsingException If the parse fails
     */
    private LeftSideItem parsePatternOrTypedHole(final String type) throws ParsingException {
        final Token next = this.scanner.getToken();
        final LeftSideItem result;
        if (next instanceof HashSymbol) {
            result = this.parseTypedHole(type);
        } else {
            result = this.parsePattern(type, next);
        }
        return result;
    }

    /**
     * Parses a sequence of tokens as a pattern descriptor.
     * @param type The type of the root node of the subtree
     * @param first First token
     * @return Subtree descriptor
     * @throws ParsingException If the parse fails
     */
    private PatternDescriptor parsePattern(final String type, final Token first)
        throws ParsingException {
        Token next = first;
        LeftDataDescriptor data = null;
        if (next instanceof OpeningAngleBracket) {
            data = this.parseData();
            next = this.scanner.getToken();
        }
        List<PatternItem> children = Collections.emptyList();
        if (next instanceof OpeningRoundBracket) {
            children = this.parseChildren();
            next = this.scanner.getToken();
        }
        if (next instanceof ClosingRoundBracket && this.nesting == 0) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "Unmatched closing parenthesis ')' found"
            );
        }
        if (next == null && this.nesting > 0) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "Unmatched opening parenthesis '(' found"
            );
        }
        this.last = next;
        return new PatternDescriptor(type, data, children);
    }

    /**
     * Parses a sequence of tokens as a data descriptor.
     * @return A data descriptor
     * @throws ParsingException If the parse fails
     */
    private LeftDataDescriptor parseData() throws ParsingException {
        final LeftDataDescriptor data;
        final Token first = this.scanner.getToken();
        if (first instanceof HashSymbol) {
            data = this.parseUntypedDataHole();
        } else if (first instanceof CharSequenceToken) {
            data = new StaticString((CharSequenceToken) first);
        } else {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "Invalid data inside data descriptor. Expected either a hole ('#...') or a string"
            );
        }
        final Token next = this.scanner.getToken();
        if (!(next instanceof ClosingAngleBracket)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "Closing angle bracket '>' expected for data descriptor"
            );
        }
        return data;
    }

    /**
     * Parses a sequence of tokens as a list of pattern children.
     * @return List of pattern children
     * @throws ParsingException If the parse fails
     */
    private List<PatternItem> parseChildren() throws ParsingException {
        final List<PatternItem> list = new ArrayList<>(0);
        do {
            final LeftSideParser parser = new LeftSideParser(
                this.scanner,
                this.nesting + 1,
                this.holes
            );
            final PatternItem child = parser.parsePatternItem();
            if (child == null) {
                break;
            }
            list.add(child);
            Token next = parser.getLastToken();
            if (next == null) {
                next = this.scanner.getToken();
            }
            if (next instanceof ClosingRoundBracket) {
                break;
            }
            if (!(next instanceof Comma)) {
                throw new CommonParsingException(
                    this.scanner.getLocation(),
                    "Child descriptors must be separated by commas"
                );
            }
        } while (true);
        return list;
    }

    /**
     * Parses a sequence of tokens as an optional item.
     * @return Left side item with {@link PatternMatchingMode#OPTIONAL} flag set
     * @throws ParsingException If the parse fails
     */
    private LeftSideItem parseOptionalItem() throws ParsingException {
        final Token first = this.scanner.getToken();
        if (!(first instanceof Identifier)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "An identifier after '[' is expected. Only patterns or typed holes can be optional"
            );
        }
        final LeftSideParser parser = new LeftSideParser(this.scanner, 0, this.holes);
        final LeftSideItem item = parser.parsePatternOrTypedHole(first.toString());
        item.setMatchingMode(PatternMatchingMode.OPTIONAL);
        Token next = parser.getLastToken();
        if (next == null) {
            next = this.scanner.getToken();
        }
        if (!(next instanceof ClosingSquareBracket)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "Missing square closing bracket ']'"
            );
        }
        return item;
    }

    /**
     * Parses a sequence of tokens as a repeated item.
     * @return Left side item with {@link PatternMatchingMode#REPEATED} flag set
     * @throws ParsingException If the parse fails
     */
    private LeftSideItem parseRepeatedItem() throws ParsingException {
        final Token first = this.scanner.getToken();
        if (!(first instanceof Identifier)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "An identifier after '{' is expected. Only patterns or typed holes can be repeated"
            );
        }
        final LeftSideParser parser = new LeftSideParser(this.scanner, 0, this.holes);
        final LeftSideItem item = parser.parsePatternOrTypedHole(first.toString());
        item.setMatchingMode(PatternMatchingMode.REPEATED);
        Token next = parser.getLastToken();
        if (next == null) {
            next = this.scanner.getToken();
        }
        if (!(next instanceof ClosingCurlyBracket)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "Missing curly closing bracket '}'"
            );
        }
        return item;
    }
}
