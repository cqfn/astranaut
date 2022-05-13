/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.uast.astgen.scanner;

import java.util.Objects;
import org.uast.astgen.exceptions.ExpectedNumber;
import org.uast.astgen.exceptions.IncorrectEllipsis;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.exceptions.UnclosedNativeCode;
import org.uast.astgen.exceptions.UnclosedString;
import org.uast.astgen.exceptions.UnknownSymbol;
import org.uast.astgen.rules.HoleAttribute;

/**
 * Scanner that splits a rule by tokens.
 *
 * @since 1.0
 */
public class Scanner {
    /**
     * The data.
     */
    private final String data;

    /**
     * The current index.
     */
    private int index;

    /**
     * Constructor.
     * @param data String that will be scanned.
     */
    public Scanner(final String data) {
        this.data = Objects.requireNonNull(data);
        this.index = 0;
    }

    /**
     * Returns next token, extracted from the source string.
     * @return A token
     * @throws ParserException Parser exception
     */
    public Token getToken() throws ParserException {
        char symbol = this.getChar();
        while (Char.isSpace(symbol)) {
            symbol = this.nextChar();
        }
        final Token result;
        if (Char.isLetter(symbol)) {
            result = this.parseIdentifier(symbol);
        } else if (Char.isBracket(symbol)) {
            this.nextChar();
            result = BracketFactory.INSTANCE.getObject(symbol);
        } else {
            result = this.parseTokenByFirstSymbol(symbol);
        }
        return result;
    }

    /**
     * Returns current char from the source sequence.
     * @return A char or 0 if the sequence is empty
     */
    private char getChar() {
        char result = 0;
        if (this.index < this.data.length()) {
            result = this.data.charAt(this.index);
        }
        return result;
    }

    /**
     * Returns next char from the source sequence.
     * @return A char or 0 if the sequence is empty
     */
    private char nextChar() {
        char result = 0;
        final int maximum =  this.data.length();
        if (this.index < maximum) {
            this.index = this.index + 1;
            if (this.index < maximum) {
                result = this.data.charAt(this.index);
            }
        }
        return result;
    }

    /**
     * Parses an identifier.
     * @param first The first symbol
     * @return A token
     */
    private Identifier parseIdentifier(final char first) {
        final StringBuilder builder = new StringBuilder();
        char symbol = first;
        do {
            builder.append(symbol);
            symbol = this.nextChar();
        } while (Char.isLetter(symbol) || Char.isDigit(symbol));
        return new Identifier(builder.toString());
    }

    /**
     * Parses a next token by first symbol.
     * @param symbol The first symbol
     * @return A token
     * @throws ParserException Parser exception
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private Token parseTokenByFirstSymbol(final char symbol) throws ParserException {
        Token result = null;
        switch (symbol) {
            case 0:
                result = Null.INSTANCE;
                break;
            case '#':
                result = this.parseHoleMarker();
                break;
            case '\"':
                result = this.parseString();
                break;
            case '$':
                result = this.parseNativeCode();
                break;
            case ',':
                this.nextChar();
                result = Comma.INSTANCE;
                break;
            case '@':
                this.nextChar();
                result = AtSign.INSTANCE;
                break;
            case '|':
                this.nextChar();
                result = VerticalBar.INSTANCE;
                break;
            case '&':
                this.nextChar();
                result = Ampersand.INSTANCE;
                break;
            case '0':
                this.nextChar();
                result = EmptySymbol.INSTANCE;
                break;
            default:
                throw new UnknownSymbol(symbol);
        }
        return result;
    }

    /**
     * Parses a hole marker.
     * @return A token
     * @throws ParserException Parser exception
     */
    private HoleMarker parseHoleMarker() throws ParserException {
        final StringBuilder number = new StringBuilder();
        char symbol = this.nextChar();
        while (symbol >= '0' && symbol <= '9') {
            number.append(symbol);
            symbol = this.nextChar();
        }
        if (number.length() == 0) {
            throw ExpectedNumber.INSTANCE;
        }
        HoleAttribute attribute = HoleAttribute.NONE;
        if (symbol == '.') {
            final StringBuilder ellipsis = new StringBuilder();
            while (symbol == '.') {
                ellipsis.append(symbol);
                symbol = this.nextChar();
            }
            if ("...".equals(ellipsis.toString())) {
                attribute = HoleAttribute.ELLIPSIS;
            } else {
                throw IncorrectEllipsis.INSTANCE;
            }
        }
        final int value = Integer.parseInt(number.toString());
        return new HoleMarker(value, attribute);
    }

    /**
     * Parses string literal.
     * @return A token
     * @throws ParserException Parser exception
     */
    private StringToken parseString() throws ParserException {
        final StringBuilder builder = new StringBuilder();
        char symbol = this.nextChar();
        while (symbol != '\"' && symbol != 0) {
            if (symbol == '\\') {
                symbol = this.nextChar();
                switch (symbol) {
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    default:
                        builder.append(symbol);
                        break;
                }
            } else {
                builder.append(symbol);
            }
            symbol = this.nextChar();
        }
        this.nextChar();
        final String value = builder.toString();
        if (symbol == 0) {
            throw new UnclosedString(value);
        }
        return new StringToken(value);
    }

    /**
     * Parses native code token.
     * @return A token
     * @throws ParserException Parser exception
     */
    private NativeCode parseNativeCode() throws ParserException {
        final StringBuilder builder = new StringBuilder();
        char symbol = this.nextChar();
        while (symbol != '$') {
            if (symbol == 0) {
                throw new UnclosedNativeCode(builder.toString());
            }
            builder.append(symbol);
            symbol = this.nextChar();
        }
        this.nextChar();
        return new NativeCode(builder.toString());
    }
}
