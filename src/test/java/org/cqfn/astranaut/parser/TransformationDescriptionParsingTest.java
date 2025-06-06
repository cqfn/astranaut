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

import org.cqfn.astranaut.dsl.RightSideItem;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link TransformationDescriptorParser} class.
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CloseResource"})
class TransformationDescriptionParsingTest {
    /**
     * Some name for a fake programming language, for testing purposes.
     */
    private static final String LANGUAGE = "common";

    @Test
    void simpleNodeToSimpleNode() {
        final String code = "ThisExpr -> This";
        final TransformationDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
        Assertions.assertEquals(1, descriptor.getLeft().size());
        final RightSideItem right = descriptor.getRight();
        Assertions.assertEquals("This", right.toString());
        Assertions.assertEquals("common", descriptor.getLanguage());
        Assertions.assertThrows(IllegalArgumentException.class, () -> descriptor.setLanguage(""));
        descriptor.setLanguage("JAVA");
        Assertions.assertEquals("java", descriptor.getLanguage());
    }

    @Test
    void repeatedTypedHole() {
        final String code = "{Statement#1} -> StatementList(#1)";
        final TransformationDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void invalidDescriptor() {
        final TransformationDescriptorParser parser = this.createParser("AAA");
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void extraSeparator() {
        final TransformationDescriptorParser parser = this.createParser("AAA -> BBB -> CCC");
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void listOfNodesToNode() {
        final String code = "AAA, BBB<'ccc'>, DDD#3, EEE<#4> -> XXX";
        final TransformationDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void extraToken() {
        final TransformationDescriptorParser parser = this.createParser("AAA -> BBB, CCC");
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void badDelimeter() {
        final TransformationDescriptorParser parser = this.createParser("AAA BBB -> CCC");
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void duplicateTypedNode() {
        final TransformationDescriptorParser parser = this.createParser(
            "AAA#1, BBB#1 -> CCC(#1)"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void duplicateUntypedHole() {
        final TransformationDescriptorParser parser = this.createParser(
            "AAA(#1, #1) -> BBB(#1)"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void inconsistentNodeHole() {
        final TransformationDescriptorParser parser = this.createParser(
            "AAA(#1) -> BBB(#2)"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void inconsistentDataHole() {
        final TransformationDescriptorParser parser = this.createParser(
            "AAA<#1> -> BBB<#2>"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void nullRightSide() {
        final String code = "AAA -> 0";
        final TransformationDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void extraTokenAfterNullRightSide() {
        final TransformationDescriptorParser parser = this.createParser(
            "AAA -> 0 BBB"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void nullInsideNode() {
        final TransformationDescriptorParser parser = this.createParser(
            "AAA -> BBB(0)"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void rightToLeft() {
        final String code = "..., AAA, BBB -> CCC";
        final TransformationDescriptor descriptor = this.parseDescriptor(code);
        Assertions.assertEquals(code, descriptor.toString());
    }

    @Test
    void onlyEllipsis() {
        final TransformationDescriptorParser parser = this.createParser(
            "... -> CCC"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    @Test
    void onlyEllipsisAndComma() {
        final TransformationDescriptorParser parser = this.createParser(
            "..., -> CCC"
        );
        Assertions.assertThrows(ParsingException.class, parser::parseDescriptor);
    }

    /**
    * Creates a {@link TransformationDescriptorParser} from the given DSL source code.
    * @param code The DSL source code to be parsed
    * @return An instance of {@link TransformationDescriptorParser}
    */
    private TransformationDescriptorParser createParser(final String code) {
        final DslReader reader = new DslReader();
        reader.setSourceCode(code);
        boolean oops = false;
        TransformationDescriptorParser parser = null;
        try {
            final Statement stmt = reader.getStatement();
            parser = new TransformationDescriptorParser(
                TransformationDescriptionParsingTest.LANGUAGE,
                stmt
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return parser;
    }

    /**
     * Parses a single descriptor from the DSL source code.
     * @param code DSL source code
     * @return Descriptor
     */
    private TransformationDescriptor parseDescriptor(final String code) {
        TransformationDescriptor descriptor = null;
        boolean oops = false;
        try {
            final TransformationDescriptorParser parser = this.createParser(code);
            descriptor = parser.parseDescriptor();
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertNotNull(descriptor);
        Assertions.assertEquals(
            TransformationDescriptionParsingTest.LANGUAGE,
            descriptor.getLanguage()
        );
        return descriptor;
    }
}
