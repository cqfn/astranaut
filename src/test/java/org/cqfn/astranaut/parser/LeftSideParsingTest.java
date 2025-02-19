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

import org.cqfn.astranaut.dsl.PatternItem;
import org.cqfn.astranaut.dsl.UntypedHole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link LeftSideParser} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class LeftSideParsingTest {
    /**
     * Fake location for testing purposes.
     */
    private static final Location LOCATION = new Location("program.goat", 1, 1);

    @Test
    void untypedHole() {
        final LeftSideParser parser = this.createParser("#17");
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof UntypedHole);
            Assertions.assertEquals(17, ((UntypedHole) item).getNumber());
            Assertions.assertEquals("#17", item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void badHole() {
        boolean oops = false;
        final LeftSideParser parser = this.createParser("#");
        try {
            parser.parsePatternItem();
        } catch (final ParsingException exception) {
            oops = true;
            Assertions.assertEquals(
                "program.goat, 1: A number is expected after '#'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void inappropriateToken() {
        boolean oops = false;
        LeftSideParser parser = this.createParser("<");
        try {
            parser.parsePatternItem();
        } catch (final ParsingException exception) {
            oops = true;
            Assertions.assertEquals(
                "program.goat, 1: Inappropriate token: '<'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
        parser = this.createParser("<");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
        parser = this.createParser(")");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
        parser = this.createParser(")");
        Assertions.assertThrows(ParsingException.class, parser::parsePatternItem);
    }

    /**
     * Creates a parser that parses an item that is part of the left side of
     *  a transformation rule.
     * @param code DSL code to parse
     * @return Left item parser
     */
    private LeftSideParser createParser(final String code) {
        final Scanner scanner = new Scanner(LeftSideParsingTest.LOCATION, code);
        return new LeftSideParser(scanner, 0);
    }
}
