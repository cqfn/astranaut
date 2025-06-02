/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.RightSideItem;
import org.cqfn.astranaut.dsl.TransformationDescriptor;

/**
 * Parser that parses transformation descriptors.
 * @since 1.0.0
 */
public final class TransformationDescriptorParser {
    /**
     * Name of the programming language whose entity this transformation descriptor covers.
     */
    private final String language;

    /**
     * Statement containing DSL code.
     */
    private final Statement stmt;

    /**
     * Flag indicating that the rule is right-associative, search direction from right to left.
     */
    private boolean direction;

    /**
     * Constructor.
     * @param language Name of the programming language whose entity this
     *  transformation descriptor cover
     * @param stmt Statement containing DSL code
     */
    public TransformationDescriptorParser(final String language, final Statement stmt) {
        this.language = language;
        this.stmt = stmt;
    }

    /**
     * Parses DSL code into a node descriptor.
     * @return A node descriptor
     * @throws ParsingException If the parse fails
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public TransformationDescriptor parseDescriptor() throws ParsingException {
        final String[] parts = this.stmt.getCode().split("->");
        if (parts.length < 2) {
            throw new CommonParsingException(this.stmt.getLocation(), "Invalid descriptor");
        }
        if (parts.length > 2) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                "One and only one '->' separator is allowed"
            );
        }
        final HoleCounter holes = new HoleCounter();
        final List<LeftSideItem> left = this.parseLeftSide(parts[0], holes);
        final RightSideItem right = this.parseRightSide(parts[1], holes);
        try {
            final TransformationDescriptor result = new TransformationDescriptor(left, right);
            result.setLanguage(this.language);
            if (this.direction) {
                result.setRightToLeftDirection();
            }
            return result;
        } catch (final IllegalArgumentException exception) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                exception.getMessage()
            );
        }
    }

    /**
     * Parses Parses the left side of the transformation rule, i.e., the list of patterns.
     * @param code Source code of the left part
     * @param holes Count of holes resulting from parsing
     * @return List of patterns containing at least one pattern
     * @throws ParsingException  If the parse fails
     */
    private List<LeftSideItem> parseLeftSide(final String code, final HoleCounter holes)
        throws ParsingException {
        final Scanner scanner = new Scanner(this.stmt.getLocation(), code);
        Token first = scanner.getToken();
        Token next;
        do {
            if (!(first instanceof Ellipsis)) {
                break;
            }
            this.direction = true;
            next = scanner.getToken();
            if (!(next instanceof Comma)) {
                throw new CommonParsingException(
                    this.stmt.getLocation(),
                    "A comma after '...' is expected"
                );
            }
            first = scanner.getToken();
            if (first == null) {
                throw new CommonParsingException(
                    this.stmt.getLocation(),
                    "You need at least one descriptor after '..., '"
                );
            }
        } while (false);
        final LeftSideParser parser = new LeftSideParser(scanner, holes);
        final List<LeftSideItem> list = new ArrayList<>(1);
        while (true) {
            final LeftSideItem item = parser.parseLeftSideItem(first);
            list.add(item);
            next = parser.getToken();
            if (next == null) {
                break;
            }
            if (!(next instanceof Comma)) {
                throw new CommonParsingException(
                    this.stmt.getLocation(),
                    "Descriptors must be separated by commas"
                );
            }
            first = parser.getToken();
        }
        return list;
    }

    /**
     * Parses Parses the right side of the transformation rule, i.e., resulting descriptor.
     * @param code Source code of the left part
     * @param holes Count of holes resulting from parsing
     * @return Resulting descriptor
     * @throws ParsingException  If the parse fails
     */
    @SuppressWarnings("PMD.PrematureDeclaration")
    private RightSideItem parseRightSide(final String code, final HoleCounter holes)
        throws ParsingException {
        final Scanner scanner = new Scanner(this.stmt.getLocation(), code);
        final RightSideItemParser parser = new RightSideItemParser(scanner, 0, holes);
        final RightSideItem item = parser.parseItem();
        Token next = parser.getLastToken();
        if (next == null) {
            next = scanner.getToken();
        }
        if (next != null) {
            throw new CommonParsingException(
                this.stmt.getLocation(),
                String.format("Extra token '%s' after the right descriptor", next.toString())
            );
        }
        return item;
    }
}
