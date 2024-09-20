/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cqfn.astranaut.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.exceptions.ExpectedDescriptor;
import org.cqfn.astranaut.exceptions.ExpectedOnlyOneEntity;
import org.cqfn.astranaut.exceptions.ExpectedSimpleIdentifier;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Disjunction;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;

/**
 * Parses children list for abstract node.
 *
 * @since 0.1.5
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
