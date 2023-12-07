/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.util.List;
import org.cqfn.astranaut.exceptions.ExpectedDescriptor;
import org.cqfn.astranaut.exceptions.ExpectedOnlyOneEntity;
import org.cqfn.astranaut.exceptions.ExpectedSimpleIdentifier;
import org.cqfn.astranaut.exceptions.NodeNameCapitalLetter;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.scanner.TokenList;

/**
 * Parser of node (class) names.
 *
 * @since 0.1.5
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
        final List<Descriptor> descriptors =
            new DescriptorsListParser(tokens).parse();
        checkDescriptorsList(descriptors);
        final Descriptor descriptor = descriptors.get(0);
        final String name = descriptor.getType();
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
