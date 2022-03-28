/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.List;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Node;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.scanner.VerticalBar;

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
        final TokenList[] segments = new Splitter(tokens)
            .split(token -> token instanceof VerticalBar);
        final List<Child> result;
        if (segments.length < 2) {
            result = new NonAbstractNodeParser(tokens).parse();
        } else {
            result = new AbstractNodeParser(segments).parse();
        }
        return result;
    }
}
