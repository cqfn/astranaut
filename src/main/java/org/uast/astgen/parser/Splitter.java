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
package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.scanner.TokenListBuilder;

/**
 * Splits a sequence of tokens according to some criteria.
 *
 * @since 1.0
 */
public class Splitter {
    /**
     * Source list.
     */
    private final TokenList source;

    /**
     * Constructor.
     * @param source Source tokens list.
     */
    public Splitter(final TokenList source) {
        this.source = source;
    }

    /**
     * Splits a sequence of tokens according to some criteria.
     * @param criteria Criteria for the splitter
     * @return Array of lists of tokens (at least 1 element)
     */
    public TokenList[] split(final SplitCriteria criteria) {
        final List<TokenList> result = new LinkedList<>();
        TokenListBuilder sequence = new TokenListBuilder();
        for (final Token token : this.source) {
            if (criteria.satisfies(token)) {
                if (!sequence.isEmpty()) {
                    result.add(sequence.createList());
                    sequence = new TokenListBuilder();
                }
            } else {
                sequence.addToken(token);
            }
        }
        if (!sequence.isEmpty()) {
            result.add(sequence.createList());
        }
        final TokenList[] array = new TokenList[result.size()];
        result.toArray(array);
        return array;
    }
}
