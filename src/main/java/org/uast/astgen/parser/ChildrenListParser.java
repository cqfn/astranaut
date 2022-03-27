/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ExpectedDescriptor;
import org.uast.astgen.exceptions.ExpectedSimpleIdentifier;
import org.uast.astgen.exceptions.NodeNameCapitalLetter;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Node;
import org.uast.astgen.scanner.TokenList;

/**
 * Parses string as a children list for the {@link Node} class.
 *
 * @since 1.0
 */
public class ChildrenListParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source Source string
     */
    public ChildrenListParser(final String source) {
        this.source = source;
    }

    /**
     * Parses source string as a children list.
     * @return A list
     * @throws ParserException If the source string can't be parsed as a chuldren list
     */
    public List<Child> parse() throws ParserException {
        final List<Child> result = new LinkedList<>();
        TokenList tokens = new Tokenizer(this.source).getTokens();
        tokens = new BracketsParser(tokens).parse();
        final List<Descriptor> descriptors = new DescriptorsListParser(tokens).parse();
        checkDescriptorsList(descriptors);
        for (final Descriptor descriptor : descriptors) {
            final String name = descriptor.getName();
            if (!checkDescriptor(descriptor)) {
                throw new ExpectedSimpleIdentifier(name);
            }
            final char first = name.charAt(0);
            if (first >= 'a' && first <= 'z') {
                throw new NodeNameCapitalLetter(name);
            }
            result.add(descriptor);
        }
        return result;
    }

    /**
     * Checks descriptors list.
     * @param list The list
     * @throws ParserException If checking filed
     */
    private static void checkDescriptorsList(final List<Descriptor> list) throws ParserException {
        final int count = list.size();
        if (count == 0) {
            throw new ExpectedDescriptor("... <- ?");
        }
    }

    /**
     * Checks the descriptor applicability.
     * @param descriptor The descriptor
     * @return Checking result
     */
    private static boolean checkDescriptor(final Descriptor descriptor) {
        return descriptor.getTag().isEmpty()
            && descriptor.getParameters().isEmpty()
            && !descriptor.getData().isValid();
    }
}
