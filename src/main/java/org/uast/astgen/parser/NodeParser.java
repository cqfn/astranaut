/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.List;
import org.uast.astgen.exceptions.BadRuleSyntax;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Node;

/**
 * Parser of {@link Node} rules.
 *
 * @since 1.0
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
        if (pair.length > 1) {
            throw BadRuleSyntax.INSTANCE;
        }
        final String left = new NodeNameParser(pair[0]).parse();
        final List<Child> right = new ChildrenListParser(pair[1]).parse();
        return new Node(left, right);
    }
}
