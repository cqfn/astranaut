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

import org.cqfn.astranaut.dsl.ResultingItem;
import org.cqfn.astranaut.dsl.UntypedHole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ResultingItemParser} class.
 * @since 1.0.0
 */
class ResultingItemParsingTest {
    /**
     * Fake location for testing purposes.
     */
    private static final Location LOCATION = new Location("program.goat", 1, 1);

    @Test
    void untypedHole() {
        final ResultingItemParser parser = this.createParser("#17");
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof UntypedHole);
            Assertions.assertEquals(17, ((UntypedHole) item).getNumber());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Creates a parser that parses an item that is part of the right side of
     *  a transformation rule.
     * @param code DSL code to parse
     * @return Resulting item parser
     */
    private ResultingItemParser createParser(final String code) {
        final Scanner scanner = new Scanner(ResultingItemParsingTest.LOCATION, code);
        return new ResultingItemParser(scanner);
    }
}
