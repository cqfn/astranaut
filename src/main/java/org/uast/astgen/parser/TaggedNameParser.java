/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.uast.astgen.exceptions.ExpectedIdentifierAfterAt;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.scanner.AtSign;
import org.uast.astgen.scanner.Identifier;
import org.uast.astgen.scanner.Token;

/**
 * Parser for tagged names.
 *
 * @since 1.0
 */
public class TaggedNameParser {
    /**
     * Stack containing unused tokens.
     */
    private final TokenStack stack;

    /**
     * Descriptor factory.
     */
    private final DescriptorFactory factory;

    /**
     * Flag that indicates that the parser has worked.
     */
    private boolean flag;

    /**
     * Constructor.
     * @param stack Stack containing unused tokens
     * @param factory Descriptor factory
     */
    public TaggedNameParser(final TokenStack stack, final DescriptorFactory factory) {
        this.stack = stack;
        this.factory = factory;
        this.flag = false;
    }

    /**
     * Parses tagged name.
     * @throws ParserException If unused tokens cannot be converted to a tagged name
     */
    public void parse() throws ParserException {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        if (this.checkCondition()) {
            if (!this.stack.hasTokens()) {
                throw ExpectedIdentifierAfterAt.INSTANCE;
            }
            final Token token = this.stack.pop();
            if (!(token instanceof Identifier)) {
                throw ExpectedIdentifierAfterAt.INSTANCE;
            }
            this.factory.replaceType(((Identifier) token).getValue());
        }
    }

    /**
     * Checks whether sequence has tagged name.
     * @return Checking result
     */
    private boolean checkCondition() {
        boolean result = false;
        if (this.stack.hasTokens()) {
            final Token token = this.stack.pop();
            if (token instanceof AtSign) {
                result = true;
            } else {
                this.stack.push(token);
            }
        }
        return result;
    }
}
