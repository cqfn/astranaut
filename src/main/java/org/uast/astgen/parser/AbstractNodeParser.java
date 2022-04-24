/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ExpectedDescriptor;
import org.uast.astgen.exceptions.ExpectedOnlyOneEntity;
import org.uast.astgen.exceptions.ExpectedSimpleIdentifier;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.rules.Disjunction;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.utils.LabelFactory;

/**
 * Parses children list for abstract node.
 *
 * @since 1.0
 */
public class AbstractNodeParser {
    /**
     * Text for exceptions.
     */
    private static final String EXPLANATORY = "... | ... | ...";

    /**
     * Text for exception with extension.
     */
    private static final String EXT_EXPLANATORY = "& | ... | ...";

    /**
     * The source token list.
     */
    private final TokenList[] segments;

    /**
     * Constructor.
     * @param segments The list of segments divided by vertical bars.
     */
    public AbstractNodeParser(final TokenList... segments) {
        this.segments = segments.clone();
    }

    /**
     * Parses children list for abstract node.
     * @return A children list
     * @throws ParserException If the list of tokens can't be parsed as a children list.
     */
    public List<Child> parse() throws ParserException {
        final List<Descriptor> composition = new LinkedList<>();
        for (final TokenList segment : this.segments) {
            final List<Descriptor> descriptors =
                new DescriptorsListParser(segment, new LabelFactory()).parse();
            checkListAbstract(descriptors);
            composition.add(descriptors.get(0));
        }
        int extension = 0;
        for (final Descriptor descriptor : composition) {
            if (descriptor.getAttribute() == DescriptorAttribute.EXT) {
                extension += 1;
            }
        }
        if (extension > 1) {
            throw new ExpectedOnlyOneEntity(AbstractNodeParser.EXT_EXPLANATORY);
        }
        final Disjunction disjunction = new Disjunction(composition);
        return Collections.singletonList(disjunction);
    }

    /**
     * Checks descriptors list for abstract node.
     * @param descriptors Descriptors list
     * @throws ParserException If checking failed
     */
    private static void checkListAbstract(final List<Descriptor> descriptors)
        throws ParserException {
        final int count = descriptors.size();
        if (count == 0) {
            throw new ExpectedDescriptor(AbstractNodeParser.EXPLANATORY);
        } else if (count > 1) {
            throw new ExpectedOnlyOneEntity(AbstractNodeParser.EXPLANATORY);
        }
        checkDescriptor(descriptors.get(0));
    }

    /**
     * Checks one descriptor for abstract node.
     * @param descriptor Descriptor
     * @throws ParserException If checking failed
     */
    private static void checkDescriptor(final Descriptor descriptor) throws ParserException {
        final boolean attribute =
            descriptor.getAttribute() == DescriptorAttribute.NONE
            || descriptor.getAttribute() == DescriptorAttribute.EXT;
        if (!attribute
            || !descriptor.getTag().isEmpty()
            || !descriptor.getParameters().isEmpty()
            || descriptor.getData().isValid()
        ) {
            throw new ExpectedSimpleIdentifier(descriptor.getType());
        }
    }
}
