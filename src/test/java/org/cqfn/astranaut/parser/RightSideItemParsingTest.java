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
import org.cqfn.astranaut.dsl.Null;
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
import org.cqfn.astranaut.dsl.RightSideItem;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.UntypedHole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link RightSideItemParser} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class RightSideItemParsingTest {
    /**
     * Fake location for testing purposes.
     */
    private static final Location LOCATION = new Location("program.goat", 1, 1);

    @Test
    void untypedHole() {
        final RightSideItemParser parser = this.createParser("#17");
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
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
        final RightSideItemParser parser = this.createParser("#");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void inappropriateToken() {
        RightSideItemParser parser = this.createParser("{");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
        parser = this.createParser(")");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void simpleName() {
        final String type = "Addition";
        final RightSideItemParser parser = this.createParser(type);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
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
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
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
        final RightSideItemParser parser = this.createParser("Identifier<?>");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void unclosedData() {
        final RightSideItemParser parser = this.createParser("Identifier<#17");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void nameAndDataAsAString() {
        final String code = "Name<'abc'>";
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
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
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
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
    void innerDescriptorWithoutChildren() {
        final String code = "StatementExpression(This())";
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertEquals("StatementExpression(This)", item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithOneChild() {
        final String code = "Return(#1)";
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof ResultingSubtreeDescriptor);
            final ResultingSubtreeDescriptor descr = (ResultingSubtreeDescriptor) item;
            Assertions.assertEquals("Return", descr.getType());
            Assertions.assertNull(descr.getData());
            final List<RightSideItem> children = descr.getChildren();
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
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithExtraComma() {
        final String code = "Subtraction(#1, #2, )";
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertEquals("Subtraction(#1, #2)", item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithThreeChildren() {
        final String code = "VariableDeclaration(#1, Identifier<#2>, #3)";
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
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
        final RightSideItemParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void missingCommaSeparator() {
        final RightSideItemParser parser = this.createParser("Addition(#1 #2)");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void invalidData() {
        final RightSideItemParser parser = this.createParser("Name<Name>");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void unmatchedOpeningParenthesis() {
        final RightSideItemParser parser = this.createParser("Name(First, Second");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void unmatchedClosingParenthesis() {
        final RightSideItemParser parser = this.createParser("Name)");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void badDataHole() {
        final RightSideItemParser parser = this.createParser("AAA<#BBB>");
        Assertions.assertThrows(ParsingException.class, parser::parseItem);
    }

    @Test
    void emptyInput() {
        final RightSideItemParser parser = this.createParser(" ");
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertNull(item);
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void nullMarker() {
        final RightSideItemParser parser = this.createParser("0");
        boolean oops = false;
        try {
            final RightSideItem item = parser.parseItem();
            Assertions.assertTrue(item instanceof Null);
            Assertions.assertEquals("0", item.toString());
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
    private RightSideItemParser createParser(final String code) {
        final Scanner scanner = new Scanner(RightSideItemParsingTest.LOCATION, code);
        return new RightSideItemParser(scanner, 0, new FakeHoleCounter());
    }

    /**
     * Fake hole counter for testing purposes (to prevent exceptions).
     * @since 1.0.0
     */
    private static class FakeHoleCounter extends HoleCounter {
        @Override
        public boolean hasNodeHole(final int number) {
            return true;
        }

        @Override
        public boolean hasDataHole(final int number) {
            return true;
        }
    }
}
