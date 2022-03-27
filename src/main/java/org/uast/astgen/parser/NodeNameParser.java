/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.List;
import org.uast.astgen.exceptions.ExpectedDescriptor;
import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.exceptions.ExpectedSimpleIdentifier;
import org.uast.astgen.exceptions.NodeNameCapitalLetter;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.scanner.TokenList;

/**
 * Parser of node (class) names.
 *
 * @since 1.0
 */
public class NodeNameParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source Source string
     */
    public NodeNameParser(final String source) {
        this.source = source;
    }

    /**
     * Parses source string as a node (class) name.
     * @return A node descriptor
     * @throws ParserException If the source string can't be parsed as a node name
     */
    public String parse() throws ParserException {
        TokenList tokens = new Tokenizer(this.source).getTokens();
        tokens = new BracketsParser(tokens).parse();
        final List<Descriptor> descriptors = new DescriptorsListParser(tokens).parse();
        checkDescriptorsList(descriptors);
        final Descriptor descriptor = descriptors.get(0);
        final String name = descriptor.getName();
        if (!checkDescriptor(descriptor)) {
            throw new ExpectedSimpleIdentifier(name);
        }
        final char first = name.charAt(0);
        if (first >= 'a' && first <= 'z') {
            throw new NodeNameCapitalLetter(name);
        }
        return name;
    }

    /**
     * Checks descriptors list.
     * @param list The list
     * @throws ParserException If checking filed
     */
    private static void checkDescriptorsList(final List<Descriptor> list) throws ParserException {
        final int count = list.size();
        if (count == 0) {
            throw new ExpectedDescriptor("? <- ...");
        }
        if (count > 1) {
            throw new ExpectedOnlyOneEntity("Name <- ...");
        }
    }

    /**
     * Checks the descriptor applicability.
     * @param descriptor The descriptor
     * @return Checking result
     */
    private static boolean checkDescriptor(final Descriptor descriptor) {
        return descriptor.getAttribute() == DescriptorAttribute.NONE
            && descriptor.getTag().isEmpty()
            && descriptor.getParameters().isEmpty()
            && !descriptor.getData().isValid();
    }
}
