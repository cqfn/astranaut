/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

import java.util.Objects;

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
     */
    public Token getToken() {
        char symbol = this.getChar();
        while (symbol == ' ' || symbol == '\n' || symbol == '\r') {
            symbol = this.nextChar();
        }
        return Null.INSTANCE;
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
}
