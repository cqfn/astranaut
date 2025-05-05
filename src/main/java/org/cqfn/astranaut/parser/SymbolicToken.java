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

/**
 * A token containing a symbol or range of symbols.
 * @since 1.0.0
 */
public abstract class SymbolicToken extends CharSequenceToken {
    /**
     * Returns the first character in the range.
     * @return Symbol
     */
    public abstract char getFirstSymbol();

    /**
     * Returns the first symbol as a Java-compatible character literal.
     * @return A quoted and escaped string representing the first symbol, suitable for Java code.
     */
    public String getFirstSymbolAsQuotedString() {
        return CharSequenceToken.toQuotedString('\'', String.valueOf(this.getFirstSymbol()));
    }

    /**
     * Returns the last character in the range.
     * @return Symbol
     */
    public abstract char getLastSymbol();

    /**
     * Returns the last symbol as a Java-compatible character literal.
     * @return A quoted and escaped string representing the last symbol, suitable for Java code.
     */
    public String getLastSymbolAsQuotedString() {
        return CharSequenceToken.toQuotedString('\'', String.valueOf(this.getLastSymbol()));
    }
}
