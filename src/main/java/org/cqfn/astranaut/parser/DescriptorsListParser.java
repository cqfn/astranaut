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

import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.exceptions.RuleCantContainHoles;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Hole;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;

/**
 * Extracts descriptors from list of tokens.
 *
 * @since 0.1.5
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
