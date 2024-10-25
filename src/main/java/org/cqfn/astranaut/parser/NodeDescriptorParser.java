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
import org.cqfn.astranaut.dsl.ListNodeDescriptor;
import org.cqfn.astranaut.dsl.LiteralDescriptor;
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
        if (parts.length < 2) {
            throw new CommonParsingException(loc, "Invalid descriptor");
        }
        if (parts.length > 2) {
            throw new CommonParsingException(loc, "One and only one '<-' separator is allowed");
        }
        final Scanner scanner = new Scanner(loc, parts[1]);
        final Token first = scanner.getToken();
        final NodeDescriptor result;
        final String name = this.parseName(parts[0]);
        if (first instanceof Zero) {
            result = this.parseNodeWithoutChildren(name, scanner);
        } else if (first instanceof OpeningCurlyBracket) {
            result = this.parseListNode(name, scanner);
        } else if (first instanceof StringToken) {
            result = this.parseLiteral(name, scanner, (StringToken) first);
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

    /**
     * Parses a list node descriptor.
     * @param name Node name
     * @param scanner Scanner
     * @return List node descriptor
     * @throws ParsingException If there are any errors on the right side of the descriptor
     */
    private ListNodeDescriptor parseListNode(final String name, final Scanner scanner)
        throws ParsingException {
        final Token identifier = scanner.getToken();
        if (!(identifier instanceof Identifier)) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "An identifier is expected after '{'"
            );
        }
        Token next = scanner.getToken();
        if (!(next instanceof ClosingCurlyBracket)) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "A closing bracket '}' is expected after the identifier"
            );
        }
        next = scanner.getToken();
        if (next != null) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "There should be no tokens after '}'"
            );
        }
        return new ListNodeDescriptor(name, identifier.toString());
    }

    /**
     * Parses a literal descriptor.
     * @param name Node name
     * @param scanner Scanner
     * @param first First token from the right side
     * @return Literal descriptor
     * @throws ParsingException If there are any errors on the right side of the descriptor
     */
    private LiteralDescriptor parseLiteral(final String name, final Scanner scanner,
        final StringToken first) throws ParsingException {
        final String type = first.getValue();
        if (type.isEmpty()) {
            throw new CommonParsingException(this.stmt.getLocation(), "Data type cannot be empty");
        }
        final LiteralDescriptor.Constructor ctor = new LiteralDescriptor.Constructor(name);
        ctor.setType(type);
        this.parseInitialValue(scanner, ctor);
        this.parseSerializerAndParser(scanner, ctor);
        this.parseLiteralException(scanner, ctor);
        final Token next = scanner.getToken();
        if (next != null) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "Redundant tokens at the end of a literal descriptor"
            );
        }
        return ctor.createDescriptor();
    }

    /**
     * Parses the initial value of the literal.
     * @param scanner Scanner
     * @param ctor Literal constructor
     * @throws ParsingException If the initial value could not be parsed
     */
    private void parseInitialValue(final Scanner scanner, final LiteralDescriptor.Constructor ctor)
        throws ParsingException {
        final String initial = this.parseNextStringToken(scanner);
        if (initial.isEmpty() && !ctor.hasPrimitiveType()) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "For a non-primitive Java type, the initial value must be specified"
            );
        }
        ctor.setInitial(initial);
    }

    /**
     * Parses the serializer code and parser code of the literal.
     * @param scanner Scanner
     * @param ctor Literal constructor
     * @throws ParsingException If the serializer code or parser code cannot be parsed
     */
    private void parseSerializerAndParser(final Scanner scanner,
        final LiteralDescriptor.Constructor ctor) throws ParsingException {
        final String serializer = this.parseNextStringToken(scanner);
        if (!ctor.hasInitial() && !serializer.isEmpty()) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "If a serializer is specified, there must also be an initial value"
            );
        }
        ctor.setSerializer(serializer);
        final String parser = this.parseNextStringToken(scanner);
        if (serializer.isEmpty() ^ parser.isEmpty()) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "Either both serializer and parser are specified, or neither are specified"
            );
        }
        if (!parser.isEmpty() && (serializer.indexOf('#') < 0 || parser.indexOf('#') < 0)) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "The serializer and parser must contain the '#' placeholder"
            );
        }
        if (!ctor.hasPrimitiveType() && !ctor.getType().equals("String") && parser.isEmpty()) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                String.format(
                    "A parser and a serializer must be specified for the '%s' type",
                    ctor.getType()
                )
            );
        }
        ctor.setParser(parser);
    }

    /**
     * Parses the exception code of the literal.
     * @param scanner Scanner
     * @param ctor Literal constructor
     * @throws ParsingException If the exception code could not be parsed
     */
    private void parseLiteralException(final Scanner scanner,
        final LiteralDescriptor.Constructor ctor) throws ParsingException {
        final String exception = this.parseNextStringToken(scanner);
        if (!exception.isEmpty() && !ctor.hasParser()) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "If an exception is specified, there must be a parser as well"
            );
        }
        ctor.setException(exception);
    }

    /**
     * Parses the following sequence of tokens, consisting of a comma and a string token.
     * @param scanner Scanner
     * @return String token value or empty string if no more tokens
     * @throws ParsingException If no further string tokens
     */
    private String parseNextStringToken(final Scanner scanner) throws ParsingException {
        final Token first = scanner.getToken();
        String value = "";
        if (first != null) {
            if (!(first instanceof Comma)) {
                throw new CommonParsingException(
                    this.stmt.getLocation(),
                    "The string literal must be followed by ','"
                );
            }
            final Token second = scanner.getToken();
            if (!(second instanceof StringToken)) {
                throw new CommonParsingException(
                    this.stmt.getLocation(),
                    "A string literal is expected after ','"
                );
            }
            value = ((StringToken) second).getValue();
        }
        return value;
    }
}
