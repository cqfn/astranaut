/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.List;
import org.uast.astgen.exceptions.CantParseSequence;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.Identifier;
import org.uast.astgen.scanner.RoundBracketsPair;
import org.uast.astgen.scanner.Token;
import org.uast.astgen.scanner.TokenList;

/**
 * Parses list of tokens as a descriptor.
 *
 * @since 1.0
 */
public class DescriptorParser {
    /**
     * The list of tokens.
     */
    private final TokenList segment;

    /**
     * Flag that indicates that the parser has worked.
     */
    private boolean flag;

    /**
     * Stack containing unused tokens.
     */
    private final TokenStack stack;

    /**
     * Constructor.
     * @param segment The list of tokens
     */
    public DescriptorParser(final TokenList segment) {
        this.segment = segment;
        this.flag = false;
        this.stack = new TokenStack(segment.iterator());
    }

    /**
     * Parses list of tokens as a descriptor.
     * @param attribute Descriptor attribute (optional, list)
     * @return A descriptor
     * @throws ParserException If the token list cannot be parsed as a descriptor
     */
    public Descriptor parse(final DescriptorAttribute attribute) throws ParserException {
        if (this.flag) {
            throw new IllegalStateException();
        }
        this.flag = true;
        assert this.stack.hasTokens();
        final Token first = this.stack.pop();
        assert first instanceof Identifier;
        final String name = ((Identifier) first).getValue();
        final DescriptorFactory factory = new DescriptorFactory(name);
        factory.setAttribute(attribute);
        new TaggedNameParser(this.stack, factory).parse();
        this.parseParameters(factory);
        new DataParser(this.stack, factory).parse();
        if (this.stack.hasTokens()) {
            throw new CantParseSequence(this.segment);
        }
        return factory.createDescriptor();
    }

    /**
     * Parses parameters inside descriptor.
     * @param factory Descriptor factory
     * @throws ParserException If unused tokens cannot be converted to a parameters list
     */
    private void parseParameters(final DescriptorFactory factory)
        throws ParserException {
        do {
            if (!this.stack.hasTokens()) {
                break;
            }
            final Token token = this.stack.pop();
            if (!(token instanceof RoundBracketsPair)) {
                this.stack.push(token);
                break;
            }
            final TokenList children = ((RoundBracketsPair) token).getTokens();
            final ParametersListParser parser = new ParametersListParser(children);
            final List<Parameter> parameters = parser.parse();
            factory.setParameters(parameters);
        } while (false);
    }
}
