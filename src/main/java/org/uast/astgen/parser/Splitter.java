/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
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
