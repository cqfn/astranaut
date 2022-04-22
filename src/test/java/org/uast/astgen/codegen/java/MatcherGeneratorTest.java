/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.parser.BracketsParser;
import org.uast.astgen.parser.DescriptorParser;
import org.uast.astgen.parser.LabelFactory;
import org.uast.astgen.parser.Tokenizer;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.utils.FilesReader;

/**
 * Tests for {@link MatcherGenerator} class.
 *
 * @since 1.0
 */
public class MatcherGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing simple case.
     */
    @Test
    public void testSimpleCase() {
        final int result = this.testing("statement", "matcher_generator_simple.txt");
        Assertions.assertEquals(1, result);
    }

    /**
     * Testing case: descriptor with one child.
     */
    @Test
    public void testDescriptorWithOneChild() {
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
    public void testExtractingData() {
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
    public void testCheckingData() {
        final int result = this.testing(
            "literal<\"+\">",
            "matcher_generator_check_data.txt"
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
