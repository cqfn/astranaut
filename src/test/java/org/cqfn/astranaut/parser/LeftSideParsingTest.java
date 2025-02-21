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
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternDescriptor;
import org.cqfn.astranaut.dsl.PatternItem;
import org.cqfn.astranaut.dsl.PatternMatchingMode;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.TypedHole;
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
        final String code = "#17";
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof UntypedHole);
            Assertions.assertEquals(17, ((UntypedHole) item).getNumber());
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(code);
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
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

    @Test
    void simpleName() {
        final String type = "Addition";
        LeftSideParser parser = this.createParser(type);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            final PatternDescriptor descr = (PatternDescriptor) item;
            Assertions.assertEquals(type, descr.getType());
            Assertions.assertNull(descr.getData());
            Assertions.assertTrue(descr.getChildren().isEmpty());
            Assertions.assertEquals(type, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(type);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            Assertions.assertEquals(type, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void typedHole() {
        final String type = "Subtraction";
        final String code = String.format("%s#19", type);
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof TypedHole);
            final TypedHole hole = (TypedHole) item;
            Assertions.assertEquals(type, hole.getType());
            Assertions.assertEquals(19, hole.getNumber());
            Assertions.assertEquals(code, hole.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(code);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof TypedHole);
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void badTypedHole() {
        final LeftSideParser parser = this.createParser("Expression#aaa");
        Assertions.assertThrows(ParsingException.class, parser::parsePatternItem);
    }

    @Test
    void nameAndDataAsAHole() {
        final String code = "Identifier<#17>";
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        parser = this.createParser(code);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void badData() {
        final String code = "Identifier<?>";
        LeftSideParser parser = this.createParser(code);
        Assertions.assertThrows(ParsingException.class, parser::parsePatternItem);
        parser = this.createParser(code);
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void unclosedData() {
        final String code = "Identifier<#17";
        LeftSideParser parser = this.createParser(code);
        Assertions.assertThrows(ParsingException.class, parser::parsePatternItem);
        parser = this.createParser(code);
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void nameAndDataAsAString() {
        final String code = "Name<'abc'>";
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            final PatternDescriptor descr = (PatternDescriptor) item;
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
        parser = this.createParser(code);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithoutChildren() {
        final String type = "Break";
        final String code = String.format("%s()", type);
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            final PatternDescriptor descr = (PatternDescriptor) item;
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
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals("StatementExpression(This)", item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithOneChild() {
        final String code = "Return(#1)";
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            final PatternDescriptor descr = (PatternDescriptor) item;
            Assertions.assertEquals("Return", descr.getType());
            Assertions.assertNull(descr.getData());
            final List<PatternItem> children = descr.getChildren();
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
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithExtraComma() {
        final String code = "Subtraction(#1, #2, )";
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals("Subtraction(#1, #2)", item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void descriptorWithThreeChildren() {
        final String code = "VariableDeclaration(#1, Identifier<#2>, #3)";
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void complexCase() {
        final String code =
            "AAA(BBB(CCC, DDD, EEE#1, #2), FFF<'ggg'>, HHH<#3>(#4, JJJ(KKK), LLL))";
        final LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void missingCommaSeparator() {
        final LeftSideParser parser = this.createParser("Addition(#1 #2)");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void invalidData() {
        final LeftSideParser parser = this.createParser("Name<Name>");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void unmatchedOpeningParenthesis() {
        final LeftSideParser parser = this.createParser("Name(First, Second");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void unmatchedClosingParenthesis() {
        final LeftSideParser parser = this.createParser("Name)");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void emptyInput() {
        LeftSideParser parser = this.createParser(" ");
        boolean oops = false;
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertNull(item);
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(" ");
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertNull(item);
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void simpleOptionalName() {
        final String type = "Operator";
        final String code = String.format("[%s]", type);
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            final PatternDescriptor descr = (PatternDescriptor) item;
            Assertions.assertEquals(type, descr.getType());
            Assertions.assertNull(descr.getData());
            Assertions.assertTrue(descr.getChildren().isEmpty());
            Assertions.assertEquals(PatternMatchingMode.OPTIONAL, descr.getMatchingMode());
            Assertions.assertEquals(code, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(code);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void optionalNameWithChildren() {
        final String code = "[Multiplication(#1, #2)]";
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof PatternDescriptor);
            final PatternDescriptor descr = (PatternDescriptor) item;
            Assertions.assertEquals(PatternMatchingMode.OPTIONAL, descr.getMatchingMode());
            Assertions.assertEquals(code, descr.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(code);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void unclosedSquareBracket() {
        final LeftSideParser parser = this.createParser("[Name");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void badOptionalDescriptor() {
        final LeftSideParser parser = this.createParser("[?]");
        Assertions.assertThrows(ParsingException.class, parser::parseLeftSideItem);
    }

    @Test
    void optionalTypedHole() {
        final String code = "[Variable#31]";
        LeftSideParser parser = this.createParser(code);
        boolean oops = false;
        try {
            final PatternItem item = parser.parsePatternItem();
            Assertions.assertTrue(item instanceof TypedHole);
            final TypedHole hole = (TypedHole) item;
            Assertions.assertEquals(PatternMatchingMode.OPTIONAL, hole.getMatchingMode());
            Assertions.assertEquals(code, hole.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        parser = this.createParser(code);
        try {
            final LeftSideItem item = parser.parseLeftSideItem();
            Assertions.assertTrue(item instanceof TypedHole);
            Assertions.assertEquals(code, item.toString());
        } catch (final ParsingException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
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
