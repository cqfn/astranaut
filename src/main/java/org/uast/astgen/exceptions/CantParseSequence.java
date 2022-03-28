/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.exceptions;

import org.uast.astgen.scanner.TokenList;

/**
 * Exception "Can't parse tokens sequence".
 *
 * @since 1.0
 */
public final class CantParseSequence extends ParserException {
    /**
     * The token sequence.
     */
    private final TokenList sequence;

    /**
     * Constructor.
     * @param sequence The token sequence
     */
    public CantParseSequence(final TokenList sequence) {
        super();
        this.sequence = sequence;
    }

    @Override
    public String getErrorMessage() {
        return new StringBuilder()
            .append("Can't parse tokens sequence: '")
            .append(this.sequence.toString())
            .append('\'')
            .toString();
    }
}
