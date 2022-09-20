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

import java.io.IOException;
import java.util.Map;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.parser.BracketsParser;
import org.cqfn.astranaut.parser.DescriptorParser;
import org.cqfn.astranaut.parser.Tokenizer;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MatcherGenerator} class.
 *
 * @since 0.1.5
 */
class MatcherGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing simple case.
     */
    @Test
    void testSimpleCase() {
        final int result = this.testing("statement", "matcher_generator_simple.txt");
        Assertions.assertEquals(1, result);
    }

    /**
     * Testing case: descriptor with one child.
     */
    @Test
    void testDescriptorWithOneChild() {
        final int result = this.testing(
            "singleExpression(literal())",
            "matcher_generator_1_child.txt"
        );
        Assertions.assertEquals(2, result);
    }

    /**
     * Testing case: extracting data.
     */
    @Test
    void testExtractingData() {
        final int result = this.testing(
            "literal<#13>",
            "matcher_generator_extract_data.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Testing case: checking data.
     */
    @Test
    void testCheckingData() {
        final int result = this.testing(
            "literal<\"+\">",
            "matcher_generator_check_data.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Testing case: extracting children.
     */
    @Test
    void testExtractingChildren() {
        final int result = this.testing(
            "simpleIdentifier(#1, #2)",
            "matcher_generator_extract_children.txt"
        );
        Assertions.assertEquals(1, result);
    }

    /**
     * Testing complex case.
     */
    @Test
    void testComplexCase() {
        final int result = this.testing(
            "singleExpression(identifier(literal<#1>), literal<\"+\">, #2)",
            "matcher_generator_complex.txt"
        );
        Assertions.assertTrue(result > 0);
    }

    /**
     * Test case: large number of child nodes.
     */
    @Test
    void testLargeNumberOfChildNodes() {
        final int result = this.testing(
            "AAA(BBB, CCC, DDD, EEE, FFF)",
            "matcher_generator_long_condition.txt"
        );
        Assertions.assertTrue(result > 0);
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
        final MatcherGenerator generator = new MatcherGenerator(env, "org.uast");
        final String name = generator.generate(descriptor);
        Assertions.assertEquals("Matcher0", name);
        final Map<String, CompilationUnit> units = generator.getUnits();
        final StringBuilder actual = new StringBuilder();
        boolean flag = false;
        for (final Map.Entry<String, CompilationUnit> entry : units.entrySet()) {
            if (flag) {
                actual.append('\n');
            }
            flag = true;
            actual.append(entry.getValue().generate());
        }
        final String expected = this.readTest(filename);
        Assertions.assertEquals(expected, actual.toString());
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
            result = new FilesReader(MatcherGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
