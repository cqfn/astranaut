/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.exceptions.RuleCantContainHoles;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.scanner.TokenList;

/**
 * Extracts descriptors from list of tokens.
 *
 * @since 1.0
 */
public class DescriptorsListParser {
    /**
     * The list of tokens.
     */
    private final TokenList tokens;

    /**
     * The label factory.
     */
    private final LabelFactory labels;

    /**
     * Constructor.
     * @param tokens The list of tokens
     * @param labels The label factory
     */
    public DescriptorsListParser(final TokenList tokens, final LabelFactory labels) {
        this.tokens = tokens;
        this.labels = labels;
    }

    /**
     * Constructor.
     * @param tokens The list of tokens
     */
    public DescriptorsListParser(final TokenList tokens) {
        this(tokens, new LabelFactory());
    }

    /**
     * Parses list of tokens as a list of descriptors.
     * @return List of descriptors (can be empty)
     * @throws ParserException If the token list cannot be parsed as a list
     *  of descriptors
     */
    public List<Descriptor> parse() throws ParserException {
        final List<Parameter> parameters =
            new ParametersListParser(this.tokens, this.labels).parse();
        final List<Descriptor> result = new LinkedList<>();
        for (final Parameter parameter : parameters) {
            if (parameter instanceof Descriptor) {
                result.add((Descriptor) parameter);
            } else if (parameter instanceof Hole) {
                throw RuleCantContainHoles.INSTANCE;
            }
        }
        return result;
    }
}
