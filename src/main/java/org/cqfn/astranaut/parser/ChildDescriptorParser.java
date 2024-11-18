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

import org.cqfn.astranaut.dsl.ChildDescriptorExt;

/**
 * Parses a child descriptor from a token sequence.
 * @since 1.0.0
 */
public class ChildDescriptorParser {
    /**
     * Flag indicating that the child node is optional.
     */
    private boolean optional;

    /**
     * Tag, that is, a name that uniquely identifies this particular child node.
     */
    private String tag;

    /**
     * Type of child node as a string.
     */
    private String type;

    /**
     * Constructor.
     */
    public ChildDescriptorParser() {
        this.optional = false;
        this.tag = "";
        this.type = "";
    }

    /**
     * Parses a child descriptor from a token sequence.
     * @param scanner Scanner
     * @param first First token of the sequence
     * @return The last token that has not been used, or {@code null} if there are no more tokens.
     * @throws ParsingException If the token sequence is not a descriptor of a child node
     */
    public Token parse(final Scanner scanner, final Token first) throws ParsingException {
        Token token;
        if (first instanceof OpeningSquareBracket) {
            this.optional = true;
            token = scanner.getToken();
        } else {
            token = first;
        }
        if (!(token instanceof Identifier)) {
            throw new CommonParsingException(
                scanner.getLocation(),
                "Expects an identifier as the type name of the child node"
            );
        }
        this.type = token.toString();
        token = scanner.getToken();
        if (token instanceof AtSymbol) {
            token = this.parseDescriptorWithTag(scanner);
        }
        if (this.optional) {
            token = ChildDescriptorParser.processClosingBracket(scanner, token);
        }
        return token;
    }

    /**
     * Creates a descriptor from the parsed data.
     * @return Descriptor
     */
    public ChildDescriptorExt createDescriptor() {
        if (this.type.isEmpty()) {
            throw new IllegalStateException();
        }
        return new ChildDescriptorExt(this.optional, this.tag, this.type);
    }

    /**
     * Parses a child descriptor containing a tag from a token sequence.
     * @param scanner Scanner
     * @return The next token that has not been used, or {@code null} if there are no more tokens.
     * @throws ParsingException If the token sequence is not a descriptor containing a tag
     */
    private Token parseDescriptorWithTag(final Scanner scanner) throws ParsingException {
        final Token token = scanner.getToken();
        if (!(token instanceof Identifier)) {
            throw new CommonParsingException(
                scanner.getLocation(),
                "An identifier is expected after '@'"
            );
        }
        this.tag = this.type;
        this.type = token.toString();
        return scanner.getToken();
    }

    /**
     * Processes the closing bracket of the optional node descriptor.
     * @param scanner Scanner
     * @param bracket Token to be a closing bracket
     * @return The next token that has not been used, or {@code null} if there are no more tokens.
     * @throws ParsingException If the optional node descriptor has not been properly closed
     */
    private static Token processClosingBracket(final Scanner scanner, final Token bracket)
        throws ParsingException {
        if (!(bracket instanceof ClosingSquareBracket)) {
            throw new CommonParsingException(
                scanner.getLocation(),
                "Missing closing bracket ']'"
            );
        }
        return scanner.getToken();
    }
}
