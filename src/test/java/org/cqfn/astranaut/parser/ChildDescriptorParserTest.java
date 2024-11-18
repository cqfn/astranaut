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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ChildDescriptorParser} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class ChildDescriptorParserTest {
    /**
     * Fake location of DSL code.
     */
    private static final Location LOCATION = new Location("test.dsl", 1, 1);

    @Test
    void simpleAndSingle() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " AAA ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertNull(token);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertFalse(descr.isOptional());
            Assertions.assertTrue(descr.getTag().isEmpty());
            Assertions.assertEquals("AAA", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void simpleAndHasNext() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " BBB, ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertTrue(token instanceof Comma);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertFalse(descr.isOptional());
            Assertions.assertTrue(descr.getTag().isEmpty());
            Assertions.assertEquals("BBB", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void taggedAndSingle() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " ccc@CCC ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertNull(token);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertFalse(descr.isOptional());
            Assertions.assertEquals("ccc", descr.getTag());
            Assertions.assertEquals("CCC", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void taggedAndHasNext() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " ddd@DDD, ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertTrue(token instanceof Comma);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertFalse(descr.isOptional());
            Assertions.assertEquals("ddd", descr.getTag());
            Assertions.assertEquals("DDD", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void optionalAndSingle() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " [EEE] ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertNull(token);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertTrue(descr.isOptional());
            Assertions.assertTrue(descr.getTag().isEmpty());
            Assertions.assertEquals("EEE", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void optionalAndHasNext() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " [FFF], ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertTrue(token instanceof Comma);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertTrue(descr.isOptional());
            Assertions.assertTrue(descr.getTag().isEmpty());
            Assertions.assertEquals("FFF", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void taggedOptionalAndSingle() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " [ggg@GGG] ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertNull(token);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertTrue(descr.isOptional());
            Assertions.assertEquals("ggg", descr.getTag());
            Assertions.assertEquals("GGG", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void taggedOptionalAndHasNext() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " [hhh@HHH], ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            final Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertTrue(token instanceof Comma);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertTrue(descr.isOptional());
            Assertions.assertEquals("hhh", descr.getTag());
            Assertions.assertEquals("HHH", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void twoInSequence() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, " XXX, [YYY] ");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        boolean oops = false;
        try {
            Token token = parser.parse(scanner, scanner.getToken());
            Assertions.assertTrue(token instanceof Comma);
            token = parser.parse(scanner, scanner.getToken());
            Assertions.assertNull(token);
            final ChildDescriptorExt descr = parser.createDescriptor();
            Assertions.assertTrue(descr.isOptional());
            Assertions.assertTrue(descr.getTag().isEmpty());
            Assertions.assertEquals("YYY", descr.getType());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void badOptional() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, "[123");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        Assertions.assertThrows(
            ParsingException.class,
            () -> parser.parse(scanner, scanner.getToken())
        );
    }

    @Test
    void notClosedOptional() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, "[AAA");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        Assertions.assertThrows(
            ParsingException.class,
            () -> parser.parse(scanner, scanner.getToken())
        );
    }

    @Test
    void badTagged() {
        final Scanner scanner = new Scanner(ChildDescriptorParserTest.LOCATION, "aaa@123");
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        Assertions.assertThrows(
            ParsingException.class,
            () -> parser.parse(scanner, scanner.getToken())
        );
    }

    @Test
    void emptyDescriptor() {
        final ChildDescriptorParser parser = new ChildDescriptorParser();
        Assertions.assertThrows(IllegalStateException.class, parser::createDescriptor);
    }
}
