/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
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
