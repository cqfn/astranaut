/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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

package org.cqfn.astranaut.codegen.java;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.parser.BracketsParser;
import org.cqfn.astranaut.parser.DescriptorParser;
import org.cqfn.astranaut.parser.Tokenizer;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.FilesReader;
import org.cqfn.astranaut.utils.LabelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MatcherGenerator} class.
 *
 * @since 1.0
 */
public class ConverterGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing simple case.
     */
    @Test
    public void testSimpleCase() {
        final int result = this.testing("Variable", "converter_generator_simple.txt");
        Assertions.assertEquals(1, result);
    }

    /**
     * Generate converter that uses data.
     */
    @Test
    public void testConverterUsesData() {
        final int result = this.testing(
            "Variable<#13>",
            "converter_generator_with_data.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Generate converter that creates node using static string as a data.
     */
    @Test
    public void testConverterStaticString() {
        final int result = this.testing(
            "Variable<\"test\">",
            "converter_generator_static_string.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Generate converter that uses children.
     */
    @Test
    public void testConverterUsesChildren() {
        final int result = this.testing(
            "Addition(#1, #2)",
            "converter_generator_with_children.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Generate converter that uses hole as descriptor.
     */
    @Test
    public void testConverterUsesHoleAsDescriptor() {
        final int result = this.testing(
            "#19",
            "converter_generator_with_hole_as_descriptor.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Testing complex case.
     */
    @Test
    public void testComplexCase() {
        final int result = this.testing(
            "VariableDeclaration(Modifier<\"public\">, #1, Identifier<#2>)",
            "converter_generator_complex.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Performs a test.
     * @param code Source code of descriptor
     * @param filename The name of file that contains the expected result
     * @return Expected generated classes number
     */
    private int testing(final String code, final String filename) {
        final Environment env = new TestEnvironment();
        final Descriptor descriptor = this.parseCode(code);
        final ConverterGenerator generator = new ConverterGenerator(env, "org.uast");
        generator.generate(descriptor, "Matcher0");
        final Map<String, CompilationUnit> units = generator.getUnits();
        final String rule = String.format("rules%sRule0", File.separator);
        Assertions.assertTrue(units.containsKey(rule));
        final String expected = this.readTest(filename);
        final String actual = units.get(rule).generate();
        Assertions.assertEquals(expected, actual);
        return units.size();
    }

    /**
     * Parses a descriptor from source code.
     * @param code DSL code
     * @return A descriptor
     */
    private Descriptor parseCode(final String code) {
        Descriptor result = null;
        boolean oops = false;
        try {
            TokenList tokens = new Tokenizer(code).getTokens();
            tokens = new BracketsParser(tokens).parse();
            result = new DescriptorParser(tokens, new LabelFactory())
                .parse(DescriptorAttribute.NONE);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }

    /**
     * Reads test source from the file.
     * @param name The file name
     * @return Test source
     */
    private String readTest(final String name) {
        String result = "";
        boolean oops = false;
        try {
            result = new FilesReader(ConverterGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
