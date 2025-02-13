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

import org.cqfn.astranaut.dsl.ResultingItem;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Parses an item that is part of the right side of a transformation rule or the whole right side
 *  of a transformation rule.
 * @since 1.0.0
 */
public class ResultingItemParser {
    /**
     * Scanner that produces a sequence of tokens.
     */
    private final Scanner scanner;

    /**
     * Constructor.
     * @param scanner Scanner
     */
    public ResultingItemParser(final Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Parses an item that is part of the right side of a transformation rule or the whole
     *  right side of a transformation rule.
     * @return Root or child element of the resulting subtree, that is, the descriptor or hole
     * @throws ParsingException If the parse fails
     */
    public ResultingItem parseItem() throws ParsingException {
        final Token first = this.scanner.getToken();
        final ResultingItem item;
        if (first instanceof HashSymbol) {
            item = this.parseUntypedHole();
        } else {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                String.format("Inappropriate token: '%s'", first.toString())
            );
        }
        return item;
    }

    /**
     * Parses a sequence of tokens as an untyped hole.
     * @return An untyped hole
     * @throws ParsingException If the parse fails
     */
    private UntypedHole parseUntypedHole() throws ParsingException {
        final Token token = this.scanner.getToken();
        if (!(token instanceof Number)) {
            throw new CommonParsingException(
                this.scanner.getLocation(),
                "A number is expected after '#'"
            );
        }
        final int value = ((Number) token).getValue();
        return UntypedHole.getInstance(value);
    }
}
