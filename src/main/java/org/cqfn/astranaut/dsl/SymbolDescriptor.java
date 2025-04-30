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
package org.cqfn.astranaut.dsl;

import org.cqfn.astranaut.codegen.java.LeftSideItemGenerator;
import org.cqfn.astranaut.codegen.java.SymbolMatcherGenerator;
import org.cqfn.astranaut.parser.SymbolicToken;

/**
 * Descriptor describing a single character, possibly with an untyped hole to extract
 *  the character's value This is used to describe the left side of a transformation rule.
 * @since 1.0.0
 */
public final class SymbolDescriptor implements PatternItem, LeftSideItem {
    /**
     * Token describing a character or range of characters.
     */
    private final SymbolicToken token;

    /**
     * Untyped hole for extracting the value of a symbol.
     */
    private final UntypedHole data;

    /**
     * The matching compatibility of the symbol descriptor.
     */
    private PatternMatchingMode mode;

    /**
     * Constructor.
     * @param token Token describing a character or range of characters
     * @param data Untyped hole for extracting the value of a symbol
     */
    public SymbolDescriptor(final SymbolicToken token, final UntypedHole data) {
        this.token = token;
        this.data = data;
        this.mode = PatternMatchingMode.NORMAL;
    }

    /**
     * Returns token describing a character or range of characters.
     * @return Token
     */
    public SymbolicToken getToken() {
        return this.token;
    }

    /**
     * Returns the data associated with this symbol.
     * @return The data descriptor
     */
    public UntypedHole getData() {
        return this.data;
    }

    @Override
    public void setMatchingMode(final PatternMatchingMode value) {
        this.mode = value;
    }

    @Override
    public PatternMatchingMode getMatchingMode() {
        return this.mode;
    }

    @Override
    public LeftSideItemGenerator createGenerator() {
        return new SymbolMatcherGenerator(this);
    }

    @Override
    public String toString(final boolean full) {
        final String result;
        if (full) {
            result = this.toFullString();
        } else {
            result = this.toShortString();
        }
        return result;
    }

    @Override
    public String toString() {
        return this.toFullString();
    }

    /**
     * Represents the descriptor as a string in short form, without matching mode.
     * @return Short pattern descriptor as a string
     */
    private String toShortString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.token.toString());
        if (this.data != null) {
            builder.append('<').append(this.data.toString()).append('>');
        }
        return builder.toString();
    }

    /**
     * Represents the descriptor as a string in full form, with matching mode.
     * @return Full pattern descriptor as a string
     */
    private String toFullString() {
        final StringBuilder builder = new StringBuilder();
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append('[');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('{');
        }
        builder.append(this.toShortString());
        if (this.mode == PatternMatchingMode.OPTIONAL) {
            builder.append(']');
        } else if (this.mode == PatternMatchingMode.REPEATED) {
            builder.append('}');
        }
        return builder.toString();
    }
}
