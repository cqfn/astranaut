/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

/**
 * Scanner splits the DSL line into tokens.
 * @since 1.0.0
 */
public final class Scanner {
    /**
     * Collection of tokens representing a single character.
     */
    private static final Map<Character, SingleCharToken> TOKENS =
        new MapUtils<Character, SingleCharToken>()
            .put(',', Comma.INSTANCE)
            .put('@', AtSymbol.INSTANCE)
            .put('|', VerticalLine.INSTANCE)
            .put('{', OpeningCurlyBracket.INSTANCE)
            .put('}', ClosingCurlyBracket.INSTANCE)
            .put('[', OpeningSquareBracket.INSTANCE)
            .put(']', ClosingSquareBracket.INSTANCE)
            .make();

    /**
     * Location of DSL code.
     */
    private final Location loc;

    /**
     * Line of DSL code.
     */
    private final String line;

    /**
     * Current index.
     */
    private int index;

    /**
     * Constructor.
     * @param loc Location of DSL code
     * @param line Line of DSL code
     */
    public Scanner(final Location loc, final String line) {
        this.loc = loc;
        this.line = line;
        this.index = 0;
    }

    /**
     * Returns location of the DSL code.
     * @return Location of the DSL code
     */
    public Location getLocation() {
        return this.loc;
    }

    /**
     * Extracts the next token from the line. If the line has ended (no more tokens),
     *  returns {@code null}.
     * @return A token
     * @throws ParsingException If the scanner cannot extract the next token
     */
    public Token getToken() throws ParsingException {
        char chr = this.getChar();
        while (Character.isWhitespace(chr)) {
            chr = this.nextChar();
        }
        final Token token;
        do {
            if (chr == '\0') {
                token = null;
                break;
            }
            if (Scanner.TOKENS.containsKey(chr)) {
                this.nextChar();
                token = Scanner.TOKENS.get(chr);
                break;
            }
            if (Character.isLetter(chr)) {
                token = this.parseIdentifier(chr);
                break;
            }
            if (Character.isDigit(chr)) {
                token = this.parseNumber(chr);
                break;
            }
            if (chr == '\'' || chr == '"') {
                token = this.parseString(chr);
                break;
            }
            throw new UnknownSymbol(this.loc, chr);
        } while (false);
        return token;
    }

    /**
     * Returns the current symbol.
     * @return A symbol
     */
    private char getChar() {
        final char chr;
        if (this.index < this.line.length()) {
            chr = this.line.charAt(this.index);
        } else {
            chr = '\0';
        }
        return chr;
    }

    /**
     * Returns the next symbol.
     * @return A symbol
     */
    private char nextChar() {
        this.index = this.index + 1;
        return this.getChar();
    }

    /**
     * Parses a sequence of characters as an identifier.
     * @param first First character
     * @return A token
     */
    private Token parseIdentifier(final char first) {
        char chr = first;
        final StringBuilder builder = new StringBuilder();
        do {
            builder.append(chr);
            chr = this.nextChar();
        } while (Character.isLetterOrDigit(chr));
        return new Identifier(builder.toString());
    }

    /**
     * Parses a sequence of characters as an integer.
     * @param first First character
     * @return A token
     */
    private Token parseNumber(final char first) {
        char chr = first;
        int value = 0;
        do {
            value = value * 10 + chr - '0';
            chr = this.nextChar();
        } while (Character.isDigit(chr));
        final Token token;
        if (value == 0) {
            token = Zero.INSTANCE;
        } else {
            token = new Number(value);
        }
        return token;
    }

    /**
     * Parses a sequence of characters as a string literal.
     * @param quote Quotation mark that opens a string
     * @return A token
     * @throws ParsingException If the string literal could not be parsed
     */
    private Token parseString(final char quote) throws ParsingException {
        char chr = this.nextChar();
        final StringBuilder builder = new StringBuilder();
        while (chr != quote) {
            if (chr == 0) {
                throw new CommonParsingException(this.loc, "String literal is not closed");
            }
            if (chr == '\\') {
                chr = this.nextChar();
                switch (chr) {
                    case 'r':
                        builder.append('\r');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case '\\':
                    case '\'':
                    case '\"':
                        builder.append(chr);
                        break;
                    default:
                        throw new CommonParsingException(
                            this.loc,
                            "Invalid string escape sequence"
                        );
                }
            } else {
                builder.append(chr);
            }
            chr = this.nextChar();
        }
        this.nextChar();
        return new StringToken(quote, builder.toString());
    }

    /**
     * Exception 'Unknown symbol'.
     * @since 1.0.0
     */
    private static final class UnknownSymbol extends ParsingException {
        /**
         * Version identifier.
         */
        private static final long serialVersionUID = -1;

        /**
         * Symbol that cannot be processed by the parser.
         */
        private final char chr;

        /**
         * Constructor.
         * @param loc Location of the code where the error was found
         * @param chr Symbol that cannot be processed by the parser.
         */
        private UnknownSymbol(final Location loc, final char chr) {
            super(loc);
            this.chr = chr;
        }

        @Override
        public String getReason() {
            return String.format("Unknown symbol: '%c'", this.chr);
        }
    }
}
