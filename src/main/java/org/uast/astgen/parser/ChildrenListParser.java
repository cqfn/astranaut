/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ExpectedDescriptor;
import org.uast.astgen.exceptions.ExpectedTaggedName;
import org.uast.astgen.exceptions.NodeNameCapitalLetter;
import org.uast.astgen.exceptions.OnlyOneListDescriptor;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
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
     * @throws ParserException If the source string can't be parsed as a children list
     */
    public List<Child> parse() throws ParserException {
        TokenList tokens = new Tokenizer(this.source).getTokens();
        tokens = new BracketsParser(tokens).parse();
        return parseNonAbstract(tokens);
    }

    /**
     * Parses children list for non-abstract node.
     * @param tokens The list of tokens.
     * @return A children list
     * @throws ParserException If the list of tokens can't be parsed as a children list.
     */
    private static List<Child> parseNonAbstract(final TokenList tokens)
        throws ParserException {
        final List<Descriptor> descriptors = new DescriptorsListParser(tokens).parse();
        checkListNonAbstract(descriptors);
        final List<Child> result = new LinkedList<>();
        for (final Descriptor descriptor : descriptors) {
            final String name = descriptor.getName();
            if (!descriptor.getParameters().isEmpty() || descriptor.getData().isValid()) {
                throw new ExpectedTaggedName(name);
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
     * Checks descriptors list for non-abstract node.
      * @param descriptors Descriptors list
     * @throws ParserException If checking failed
     */
    private static void checkListNonAbstract(final List<Descriptor> descriptors)
        throws ParserException {
        final int count = descriptors.size();
        if (count == 0) {
            throw new ExpectedDescriptor("... <- ?");
        } else if (count > 1) {
            for (final Descriptor descriptor : descriptors) {
                if (descriptor.getAttribute() == DescriptorAttribute.LIST) {
                    throw OnlyOneListDescriptor.INSTANCE;
                }
            }
        }
    }
}
