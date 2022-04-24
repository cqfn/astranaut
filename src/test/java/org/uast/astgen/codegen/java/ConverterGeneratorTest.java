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
import org.uast.astgen.parser.Tokenizer;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;
import org.uast.astgen.scanner.TokenList;
import org.uast.astgen.utils.FilesReader;
import org.uast.astgen.utils.LabelFactory;

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
        final String rule = "Rule0";
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
