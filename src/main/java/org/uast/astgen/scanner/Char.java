/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

/**
 * Routines for characters processing.
 *
 * @since 1.0
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
