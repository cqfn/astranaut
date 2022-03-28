/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Child;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Disjunction;
import org.uast.astgen.scanner.TokenList;

/**
 * Parses children list for abstract node.
 *
 * @since 1.0
 */
public class AbstractNodeParser {
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
            final List<Descriptor> descriptors = new DescriptorsListParser(segment).parse();
            composition.add(descriptors.get(0));
        }
        final Disjunction disjunction = new Disjunction(composition);
        return Collections.singletonList(disjunction);
    }
}
