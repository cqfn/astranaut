/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
import org.cqfn.astranaut.exceptions.BadRuleSyntax;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Child;
import org.cqfn.astranaut.rules.Node;

/**
 * Parser of {@link Node} rules.
 *
 * @since 0.1.5
 */
public class NodeParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source Source string
     */
    public NodeParser(final String source) {
        this.source = source;
    }

    /**
     * Parses source string as a {@link Node} descriptor.
     * @return A node descriptor
     * @throws ParserException If the source string can't be parsed as a node descriptor
     */
    public Node parse() throws ParserException {
        assert this.source.contains("<-");
        final String[] pair = this.source.split("<-");
        if (pair.length > 2) {
            throw BadRuleSyntax.INSTANCE;
        }
        final String left = new NodeNameParser(pair[0]).parse();
        final List<Child> right = new ChildrenListParser(pair[1]).parse();
        return new Node(left, right);
    }
}
