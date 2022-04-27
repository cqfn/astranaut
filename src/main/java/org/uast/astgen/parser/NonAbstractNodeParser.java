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
import org.uast.astgen.rules.Empty;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.utils.LabelFactory;

/**
 * Parses children list for non-abstract node.
 *
 * @since 1.0
 */
public class NonAbstractNodeParser {
    /**
     * The source token list.
     */
    private final TokenList tokens;

    /**
     * Constructor.
     * @param tokens The source token list
     */
    public NonAbstractNodeParser(final TokenList tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses children list for non-abstract node.
     * @return A children list
     * @throws ParserException If the list of tokens can't be parsed as a children list.
     */
    public List<Child> parse() throws ParserException {
        final List<Descriptor> descriptors =
            new DescriptorsListParser(this.tokens, new LabelFactory()).parse();
        checkListNonAbstract(descriptors);
        final List<Child> result = new LinkedList<>();
        for (final Descriptor descriptor : descriptors) {
            final String name = descriptor.getType();
            if (descriptor.equals(Empty.INSTANCE)) {
                result.add(descriptor);
                break;
            }
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
