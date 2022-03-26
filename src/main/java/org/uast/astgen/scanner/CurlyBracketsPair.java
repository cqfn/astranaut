/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.scanner;

import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.DescriptorAttribute;

/**
 * Token that represents a pair of curly brackets and list of tokens between.
 *
 * @since 1.0
 */
public class CurlyBracketsPair extends BracketsPair {
    /**
     * Constructor.
     *
     * @param tokens The list of tokens.
     */
    public CurlyBracketsPair(final TokenList tokens) {
        super(tokens);
    }

    @Override
    public final char getOpeningBracket() {
        return '{';
    }

    @Override
    public final char getClosingBracket() {
        return '}';
    }

    @Override
    public final DescriptorAttribute getDescriptorAttribute() throws ParserException {
        return DescriptorAttribute.LIST;
    }
}
