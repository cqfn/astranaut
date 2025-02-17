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

import java.util.List;
import org.cqfn.astranaut.dsl.DataDescriptor;
import org.cqfn.astranaut.dsl.ResultingItem;
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.UntypedHole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ResultingItemParser} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
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
            Assertions.assertEquals("#17", item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void badHole() {
        final ResultingItemParser parser = this.createParser("#");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void inappropriateToken() {
        final ResultingItemParser parser = this.createParser("{");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void simpleName() {
        final String type = "Addition";
        final ResultingItemParser parser = this.createParser(type);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof ResultingSubtreeDescriptor);
            final ResultingSubtreeDescriptor descr = (ResultingSubtreeDescriptor) item;
            Assertions.assertEquals(type, descr.getType());
            Assertions.assertNull(descr.getData());
            Assertions.assertTrue(descr.getChildren().isEmpty());
            Assertions.assertEquals(type, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void nameAndDataAsAHole() {
        final String code = "Identifier<#17>";
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof ResultingSubtreeDescriptor);
            final ResultingSubtreeDescriptor descr = (ResultingSubtreeDescriptor) item;
            Assertions.assertEquals("Identifier", descr.getType());
            Assertions.assertTrue(descr.getData() instanceof UntypedHole);
            Assertions.assertTrue(descr.getChildren().isEmpty());
            Assertions.assertEquals(code, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void badData() {
        final ResultingItemParser parser = this.createParser("Identifier<?>");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void unclosedData() {
        final ResultingItemParser parser = this.createParser("Identifier<#17");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void nameAndDataAsAString() {
        final String code = "Name<'abc'>";
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof ResultingSubtreeDescriptor);
            final ResultingSubtreeDescriptor descr = (ResultingSubtreeDescriptor) item;
            Assertions.assertEquals("Name", descr.getType());
            final DataDescriptor data = descr.getData();
            Assertions.assertTrue(data instanceof StaticString);
            Assertions.assertEquals("abc", ((StaticString) data).getValue());
            Assertions.assertTrue(descr.getChildren().isEmpty());
            Assertions.assertEquals(code, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithoutChildren() {
        final String type = "Break";
        final String code = String.format("%s()", type);
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof ResultingSubtreeDescriptor);
            final ResultingSubtreeDescriptor descr = (ResultingSubtreeDescriptor) item;
            Assertions.assertEquals(type, descr.getType());
            Assertions.assertNull(descr.getData());
            Assertions.assertTrue(descr.getChildren().isEmpty());
            Assertions.assertEquals(type, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithOneChild() {
        final String code = "Return(#1)";
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof ResultingSubtreeDescriptor);
            final ResultingSubtreeDescriptor descr = (ResultingSubtreeDescriptor) item;
            Assertions.assertEquals("Return", descr.getType());
            Assertions.assertNull(descr.getData());
            final List<ResultingItem> children = descr.getChildren();
            Assertions.assertEquals(1, children.size());
            Assertions.assertTrue(children.get(0) instanceof UntypedHole);
            Assertions.assertEquals(code, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithTwoChildren() {
        final String code = "Addition(#1, #2)";
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithThreeChildren() {
        final String code = "VariableDeclaration(#1, Identifier<#2>, #3)";
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void complexCase() {
        final String code =
            "AAA(BBB(CCC, DDD, EEE, #1), FFF<'ggg'>, HHH<'iii'>(#2, JJJ(KKK), LLL))";
        final ResultingItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final ResultingItem item = parser.parseItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void missingCommaSeparator() {
        final ResultingItemParser parser = this.createParser("Addition(#1 #2)");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void invalidData() {
        final ResultingItemParser parser = this.createParser("Name<Name>");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void unmatchedOpeningParenthesis() {
        final ResultingItemParser parser = this.createParser("Name(First, Second");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void unmatchedClosingParenthesis() {
        final ResultingItemParser parser = this.createParser("Name)");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    /**
     * Creates a parser that parses an item that is part of the right side of
     *  a transformation rule.
     * @param code DSL code to parse
     * @return Resulting item parser
     */
    private ResultingItemParser createParser(final String code) {
        final Scanner scanner = new Scanner(ResultingItemParsingTest.LOCATION, code);
        return new ResultingItemParser(scanner, 0);
    }
}
