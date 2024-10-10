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
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;

/**
 * Parser that parses node descriptors.
 * @since 1.0.0
 */
public class NodeDescriptorParser {
    /**
     * Name of the programming language whose entity this node descriptor covers.
     */
    private final String language;

    /**
     * Statement containing DSL code.
     */
    private final Statement stmt;

    /**
     * Constructor.
     * @param language Name of the programming language whose entity this node descriptor cover
     * @param stmt Statement containing DSL code
     */
    public NodeDescriptorParser(final String language, final Statement stmt) {
        this.language = language;
        this.stmt = stmt;
    }

    /**
     * Parses DSL code into a node descriptor.
     * @return A node descriptor
     * @throws ParsingException If the parse fails
     */
    public NodeDescriptor parseDescriptor() throws ParsingException {
        final Location loc = this.stmt.getLocation();
        final String[] parts = this.stmt.getCode().split("<-");
        if (parts.length != 2) {
            throw new CommonParsingException(loc, "One and only one '<-' separator is allowed");
        }
        final Scanner scanner = new Scanner(loc, parts[1]);
        final Token first = scanner.getToken();
        if (first == null) {
            throw new CommonParsingException(loc, "There is no description of child nodes");
        }
        final NodeDescriptor result;
        if (first instanceof Zero) {
            final String name = this.parseName(parts[0]);
            result = this.parseNodeWithoutChildren(name, scanner);
        } else {
            throw new CommonParsingException(
                loc,
                String.format("Inappropriate token: '%s'", first.toString())
            );
        }
        result.setLanguage(this.language);
        return result;
    }

    /**
     * Parses the node name (left part of the descriptor).
     * @param left Left part of the descriptor
     * @return Node name
     * @throws ParsingException If the left part could not be recognized
     */
    private String parseName(final String left) throws ParsingException {
        final Location loc = this.stmt.getLocation();
        final Scanner scanner = new Scanner(loc, left);
        final Token first = scanner.getToken();
        final Token next = scanner.getToken();
        if (!(first instanceof Identifier) || next != null) {
            throw new CommonParsingException(
                loc,
                "Can't parse the node name. This name must consist of only one identifier"
            );
        }
        return first.toString();
    }

    /**
     * Parses a node descriptor with no child nodes.
     * @param name Node name
     * @param scanner Scanner
     * @return Node descriptor without child nodes
     * @throws ParsingException If there are any errors on the right side of the descriptor
     */
    private RegularNodeDescriptor parseNodeWithoutChildren(final String name, final Scanner scanner)
        throws ParsingException {
        final Token next = scanner.getToken();
        if (next != null) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "There should be no tokens after '0'"
            );
        }
        return new RegularNodeDescriptor(name, Collections.emptyList());
    }
}
