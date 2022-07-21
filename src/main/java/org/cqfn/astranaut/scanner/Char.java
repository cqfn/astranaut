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
package org.cqfn.astranaut.scanner;

/**
 * Routines for characters processing.
 *
 * @since 0.1.5
 */
final class Char {
    /**
     * Constructor.
     */
    private Char() {
    }

    /**
     * Checks whether symbol is space.
     * @param symbol A symbol
     * @return Result of checking
     */
    static boolean isSpace(final char symbol) {
        return symbol == ' ' || symbol == '\n' || symbol == '\r';
    }

    /**
     * Checks whether symbol is a small letter.
     * @param symbol A symbol
     * @return Result of checking
     */
    static boolean isSmallLetter(final char symbol) {
        return symbol >= 'a' && symbol <= 'z';
    }

    /**
     * Checks whether symbol is a capital letter.
     * @param symbol A symbol
     * @return Result of checking
     */
    static boolean isCapitalLetter(final char symbol) {
        return symbol >= 'A' && symbol <= 'Z';
    }

    /**
     * Checks whether symbol is a letter.
     * @param symbol A symbol
     * @return Result of checking
     */
    static boolean isLetter(final char symbol) {
        return Char.isSmallLetter(symbol) || Char.isCapitalLetter(symbol)
            || symbol == '_';
    }

    /**
     * Checks whether symbol is a digit.
     * @param symbol A symbol
     * @return Result of checking
     */
    static boolean isDigit(final char symbol) {
        return symbol >= '0' && symbol <= '9';
    }

    /**
     * Checks whether symbol is a bracket.
     * @param symbol A symbol
     * @return Result of checking
     */
    static boolean isBracket(final char symbol) {
        return "{}()[]<>".indexOf(symbol) != -1;
    }
}
