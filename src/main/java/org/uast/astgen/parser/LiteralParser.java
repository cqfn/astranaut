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

package org.uast.astgen.parser;

import java.util.Iterator;
import java.util.List;
import org.uast.astgen.exceptions.BadRuleSyntax;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Literal;

/**
 * Parser of {@link Literal} rules.
 *
 * @since 1.0
 */
public class LiteralParser {
    /**
     * Expected parameters count.
     */
    private static final int EXPECTED_COUNT = 3;

    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source Source string
     */
    public LiteralParser(final String source) {
        this.source = source;
    }

    /**
     * Parses source string as a {@link Literal} descriptor.
     * @return A node descriptor
     * @throws ParserException If the source string can't be parsed as a literal descriptor
     */
    public Literal parse() throws ParserException {
        assert this.source.contains("<-");
        final String[] pair = this.source.split("<-");
        if (pair.length > 2) {
            throw BadRuleSyntax.INSTANCE;
        }
        final String left = new NodeNameParser(pair[0]).parse();
        final List<String> right = new LiteralParametersParser(pair[1]).parse();
        assert right.size() >= LiteralParser.EXPECTED_COUNT;
        final Iterator<String> iterator = right.iterator();
        final Literal.Builder builder = new Literal.Builder();
        builder.setType(left);
        builder.setKlass(iterator.next());
        builder.setStringifier(iterator.next());
        builder.setParser(iterator.next());
        if (iterator.hasNext()) {
            builder.setException(iterator.next());
        }
        return builder.build();
    }
}
