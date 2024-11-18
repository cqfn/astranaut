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

import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.ListNodeDescriptor;
import org.cqfn.astranaut.dsl.LiteralDescriptor;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link NodeDescriptorParser} class.
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CloseResource"})
class NodeDescriptionParsingTest {
    /**
     * Some name for a fake programming language, for testing purposes.
     */
    private static final String LANGUAGE = "common";

    @Test
    void nodeWithoutChildren() {
        final String code = "This <- 0";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertEquals("This", descriptor.getName());
        Assertions.assertTrue(descriptor instanceof RegularNodeDescriptor);
        Assertions.assertEquals(
            0,
            ((RegularNodeDescriptor) descriptor).getExtChildTypes().size()
        );
    }

    @Test
    void listNode() {
        final String code = "Arguments <- {Expression}";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertEquals("Arguments", descriptor.getName());
        Assertions.assertTrue(descriptor instanceof ListNodeDescriptor);
        Assertions.assertEquals(
            "Expression",
            ((ListNodeDescriptor) descriptor).getChildType()
        );
    }

    @Test
    void tooManySeparators() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- BBB <- CCC")
        );
        boolean oops = false;
        try {
            parser.parseDescriptor();
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Parser", exception.getInitiator());
            Assertions.assertEquals(
                "test.dsl, 1: One and only one '<-' separator is allowed",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void missingName() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(" <- BBB")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void badName() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("13 <- 0")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void nameWithExtraToken() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA, BBB <- 0")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void missingRightPart() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <-")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void tokensAfterZero() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- 0, BBB")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noTokensAfterOpeningCurlyBracket() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- {")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noIdentifierAfterOpeningCurlyBracket() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- {0")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noClosingCurlyBracket() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- {BBB")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void extraTokenClosingCurlyBracket() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- {BBB},")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void inappropriateToken() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("AAA <- 13")
        );
        boolean oops = false;
        try {
            parser.parseDescriptor();
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals(
                "test.dsl, 1: Inappropriate token: '13'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void literalWithTypeOnly() {
        final String code = "IntegerLiteral <- 'int'";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertTrue(descriptor instanceof LiteralDescriptor);
    }

    @Test
    void literalWithEmptyType() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("Literal <- ''")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void literalWithInitialValue() {
        final String code = "IntegerLiteral <- 'int', '0'";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertTrue(descriptor instanceof LiteralDescriptor);
    }

    @Test
    void literalWithoutInitialValue() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement("Literal <- 'String'")
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void literalWithSerializerAndParser() {
        final String code =
            "IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(#)'";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertTrue(descriptor instanceof LiteralDescriptor);
    }

    @Test
    void literalWithoutInitialValueAndSerializer() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '', 'String.valueOf(#)', 'Integer.parseInt(#)'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void literalWithSerializerButWithoutParser() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '0', 'String.valueOf(#)'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void literalWithoutParserButWithSerializer() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '0', '', 'Integer.parseInt(#)'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void serializerWithoutPlaceholder() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '0', 'String.valueOf(value)', 'Integer.parseInt(#)'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void parserWithoutPlaceholder() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(value)'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void literalBasedOnObjectWithoutParser() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Literal <- 'Object', 'null'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void fullLiteralDescriptor() {
        final String code =
            "IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(#)', 'NumberFormatException'";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertTrue(descriptor instanceof LiteralDescriptor);
    }

    @Test
    void literalWithExceptionButWithoutParser() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '0', '', '', 'NumberFormatException'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void regularNodeWithOneChild() {
        final String code = "AAA <- BBB";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof RegularNodeDescriptor);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void regularNodeWithTwoChildren() {
        final String code = "AAA <- BBB, CCC";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof RegularNodeDescriptor);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void regularNodeWithThreeChildren() {
        final String code = "AAA <- BBB, CCC, DDD";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof RegularNodeDescriptor);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void regularNodeWithOptionalAndTaggedChildren() {
        final String code = "AAA <- [BBB], ccc@CCC";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof RegularNodeDescriptor);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void abstractNodeWithTwoSubtypes() {
        final String code = "AAA <- BBB | CCC";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof AbstractNodeDescriptor);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void abstractNodeWithThreeSubtypes() {
        final String code = "AAA <- BBB | CCC | DDD";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof AbstractNodeDescriptor);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void abstractNodeWithZeroAtTheEnd() {
        final String code = "AAA <- BBB | DDD | 0";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof AbstractNodeDescriptor);
        Assertions.assertEquals("AAA <- BBB | DDD", descriptor.toString());
    }

    @Test
    void abstractNodeWithOneSubtype() {
        final String code = "AAA <- EEE | 0";
        final NodeDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertTrue(descriptor instanceof AbstractNodeDescriptor);
        Assertions.assertEquals("AAA <- EEE | ?", descriptor.toString());
    }

    @Test
    void extraTokensAfterLiteral() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', '0', 'String.valueOf(#)', 'Integer.parseInt(#)', 'NumberFormatException', xxx"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noCommaAfterStringToken() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int' '0'"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void badTokenAfterComma() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "IntegerLiteral <- 'int', xxx"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noSeparator() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Expression <- Literal BooleanExpression"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void optionalChildInAbstractNode() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Expression <- [Literal] | BooleanExpression"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void taggedChildInAbstractNode() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Expression <- xxx@Literal | BooleanExpression"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noVerticalLine() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Expression <- Literal | BooleanExpression BinaryOperator"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void noIdentifierAfterVerticalLine() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Expression <- Literal | 13"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void extraTokensAfterZero() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "Expression <- Literal | 0,"
            )
        );
        boolean oops = false;
        try {
            parser.parseDescriptor();
        } catch (final ParsingException exception) {
            oops = true;
            Assertions.assertEquals(
                "There should be no tokens after '0'",
                exception.getReason()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void noComma() {
        final NodeDescriptorParser parser = new NodeDescriptorParser(
            NodeDescriptionParsingTest.LANGUAGE,
            this.createStatement(
                "VariableDeclaration <- [Identifier], Identifier Expression"
            )
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    /**
     * Parses a single descriptor from the DSL source code.
     * @param code DSL source code
     * @return Descriptor
     */
    private NodeDescriptor parseDescriptor(final String code) {
        final DslReader reader = new DslReader();
        reader.setSourceCode(code);
        boolean oops = false;
        NodeDescriptor descriptor = null;
        try {
            final Statement stmt = reader.getStatement();
            final NodeDescriptorParser parser = new NodeDescriptorParser(
                NodeDescriptionParsingTest.LANGUAGE,
                stmt
            );
            descriptor = parser.parseDescriptor();
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertNotNull(descriptor);
        Assertions.assertEquals(NodeDescriptionParsingTest.LANGUAGE, descriptor.getLanguage());
        return descriptor;
    }

    /**
     * Creates statement from DSL source code.
     * @param code DSL source code
     * @return Statement
     */
    private Statement createStatement(final String code) {
        final Statement.Constructor ctor = new Statement.Constructor();
        ctor.setFilename("test.dsl");
        ctor.setBegin(1);
        ctor.setEnd(1);
        ctor.setCode(code);
        return ctor.createStatement();
    }
}
