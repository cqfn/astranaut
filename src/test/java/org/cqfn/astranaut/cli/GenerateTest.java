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
package org.cqfn.astranaut.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Locale;
import java.util.Set;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests checking file generation for different rule sets.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class GenerateTest extends EndToEndTest {
    /**
     * Name of the parser module that initiates the exception.
     */
    private static final String PARSER_EXCERT = "Parser";

    /**
     * Name of the analyzer module that initiates the exception.
     */
    private static final String ANALYZER_EXCERT = "Analyzer";

    @Test
    void nodeWithoutChildren(final @TempDir Path temp) {
        final String expected = this.loadStringResource("node_without_children.txt");
        final String actual = this.run("node_without_children.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithOneChild(final @TempDir Path temp) {
        final String expected = this.loadStringResource("node_with_one_child.txt");
        final String actual = this.run("node_with_one_child.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithTwoChildren(final @TempDir Path temp) {
        final String expected = this.loadStringResource("node_with_two_children.txt");
        final String actual = this.run("node_with_two_children.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithThreeChildren(final @TempDir Path temp) {
        final String expected = this.loadStringResource("node_with_three_children.txt");
        final String actual = this.run("node_with_three_children.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithMultipleInheritance(final @TempDir Path temp) {
        final String expected = this.loadStringResource("node_with_multiple_inheritance.txt");
        final String actual = this.run("node_with_multiple_inheritance.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void listNode(final @TempDir Path temp) {
        final String expected = this.loadStringResource("list_node.txt");
        final String actual = this.run("list_node.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void literals(final @TempDir Path temp) {
        final String expected = this.loadStringResource("literals.txt");
        final String actual = this.run("literals.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void languageDefined(final @TempDir Path temp) {
        final String expected = this.loadStringResource("language_defined.txt");
        final String actual = this.run("language_defined.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodesFromDifferentLanguages(final @TempDir Path temp) {
        final String expected = this.loadStringResource("nodes_from_different_languages.txt");
        final String actual = this.run("nodes_from_different_languages.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void readBadDsl(final @TempDir Path temp) {
        final String message = this.runAndReadErrorMessage(
            "rule_without_separator.dsl",
            temp,
            GenerateTest.PARSER_EXCERT
        );
        Assertions.assertEquals(
            "rule_without_separator.dsl, 26: The rule does not contain a separator",
            message
        );
    }

    @Test
    void badAbstractNode(final @TempDir Path temp) {
        final String message = this.runAndReadErrorMessage(
            "bad_abstract_node.dsl",
            temp,
            GenerateTest.ANALYZER_EXCERT
        );
        Assertions.assertEquals(
            "bad_abstract_node.dsl, 31: The abstract node 'Expression' is the base for the node 'UnaryExpression' which is not defined",
            message
        );
    }

    @Test
    void notDefinedChildType(final @TempDir Path temp) {
        final String message = this.runAndReadErrorMessage(
            "not_defined_child_type.dsl",
            temp,
            GenerateTest.ANALYZER_EXCERT
        );
        Assertions.assertEquals(
            "not_defined_child_type.dsl, 29: The 'Assignment' node contains a child node 'LeftExpression' which is not defined",
            message
        );
    }

    @Test
    void writeToProtectedFile(final @TempDir Path temp) {
        boolean oops = false;
        try {
            final Path dir = temp.resolve("output");
            final Path readonly = temp.resolve("output/org/cqfn/uast/tree/package-info.java");
            new FilesWriter(readonly.toFile().getAbsolutePath()).writeStringNoExcept("xxx");
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
                Files.setAttribute(readonly, "dos:readonly", true);
            } else {
                final Set<PosixFilePermission> permissions =
                    Files.getPosixFilePermissions(readonly);
                permissions.remove(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(readonly, permissions);
            }
            String message = "";
            final String[] args = {
                "generate",
                "src/test/resources/dsl/node_without_children.dsl",
                "--output",
                dir.toFile().getAbsolutePath(),
                "--package",
                "org.cqfn.uast.tree",
                "--license",
                "LICENSE.txt",
                "--version",
                "1.0.0",
            };
            boolean wow = false;
            try {
                Main.run(args);
            } catch (final BaseException exception) {
                wow = true;
                message = exception.getErrorMessage();
            }
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
                Files.setAttribute(readonly, "dos:readonly", false);
            } else {
                final Set<PosixFilePermission> permissions =
                    Files.getPosixFilePermissions(readonly);
                permissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(readonly, permissions);
            }
            Assertions.assertTrue(wow);
            Assertions.assertEquals("Cannot write file: 'package-info.java'", message);
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void writeToProtectedFolder(final @TempDir Path temp) {
        final Path dir = temp.resolve("output");
        this.createWriteProtectedFolder(dir);
        String message = "";
        final String[] args = {
            "generate",
            "src/test/resources/dsl/node_without_children.dsl",
            "--output",
            dir.toFile().getAbsolutePath(),
            "--package",
            "org.cqfn.uast.tree",
            "--license",
            "LICENSE.txt",
            "--version",
            "1.0.0",
        };
        boolean wow = false;
        try {
            Main.run(args);
        } catch (final BaseException exception) {
            wow = true;
            message = exception.getErrorMessage();
        }
        this.clearWriteProtectedFlag(dir);
        Assertions.assertTrue(wow);
        Assertions.assertTrue(message.startsWith("Cannot create destination folder"));
    }

    @Test
    void differentLevelsOfInheritance(final @TempDir Path temp) {
        final String expected = this.loadStringResource("different_levels_of_inheritance.txt");
        final String actual = this.run("different_levels_of_inheritance.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void simpleConversionRule(final @TempDir Path temp) {
        final String expected = this.loadStringResource("simple_conversion_rule.txt");
        final String actual = this.run("simple_conversion_rule.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void conversionWithStaticStrings(final @TempDir Path temp) {
        final String expected = this.loadStringResource("conversion_with_static_strings.txt");
        final String actual = this.run("conversion_with_static_strings.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void matcherReuse(final @TempDir Path temp) {
        final String expected = this.loadStringResource("matcher_reuse.txt");
        final String actual = this.run("matcher_reuse.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void conversionWithOptionalNode(final @TempDir Path temp) {
        final String expected = this.loadStringResource("conversion_with_optional_node.txt");
        final String actual = this.run("conversion_with_optional_node.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void conversionToListNode(final @TempDir Path temp) {
        final String expected = this.loadStringResource("conversion_to_list_node.txt");
        final String actual = this.run("conversion_to_list_node.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void extractNodeFromComplexCase(final @TempDir Path temp) {
        final String expected = this.loadStringResource("extract_node_from_complex_case.txt");
        final String actual = this.run("extract_node_from_complex_case.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void extractNodeFromSimpleCase(final @TempDir Path temp) {
        final String expected = this.loadStringResource("extract_node_from_simple_case.txt");
        final String actual = this.run("extract_node_from_simple_case.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void complexCaseConversion(final @TempDir Path temp) {
        final String expected = this.loadStringResource("complex_case_conversion.txt");
        final String actual = this.run("complex_case_conversion.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void untypedHoles(final @TempDir Path temp) {
        final String expected = this.loadStringResource("untyped_holes.txt");
        final String actual = this.run("untyped_holes.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void complexResultingNode(final @TempDir Path temp) {
        final String expected = this.loadStringResource("complex_resulting_node.txt");
        final String actual = this.run("complex_resulting_node.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void dataExtraction(final @TempDir Path temp) {
        final String expected = this.loadStringResource("data_extraction.txt");
        final String actual = this.run("data_extraction.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void conversionsWithCommonRules(final @TempDir Path temp) {
        final String expected = this.loadStringResource("conversions_with_common_rules.txt");
        final String actual = this.run("conversions_with_common_rules.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void conversionsWithoutCommonRules(final @TempDir Path temp) {
        final String expected = this.loadStringResource("conversions_without_common_rules.txt");
        final String actual = this.run("conversions_without_common_rules.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void matchOptionalAndRepeated(final @TempDir Path temp) {
        final String expected = this.loadStringResource("match_optional_and_repeated.txt");
        final String actual = this.run("match_optional_and_repeated.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void matchOptionalAndRepeatedWithData(final @TempDir Path temp) {
        final String expected = this.loadStringResource(
            "match_optional_and_repeated_with_data.txt"
        );
        final String actual = this.run("match_optional_and_repeated_with_data.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void oneOptionalNodeOnLeft(final @TempDir Path temp) {
        final String message = this.runAndReadErrorMessage(
            "one_optional_on_left.dsl",
            temp,
            GenerateTest.PARSER_EXCERT
        );
        Assertions.assertEquals(
            "one_optional_on_left.dsl, 26: At least one node on the left must be guaranteed to be consumed",
            message
        );
    }

    @Test
    void extractFirstNodeFromList(final @TempDir Path temp) {
        final String expected = this.loadStringResource("extract_first_node_from_list.txt");
        final String actual = this.run("extract_first_node_from_list.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void symbolicPatterns(final @TempDir Path temp) {
        final String expected = this.loadStringResource("symbolic_patterns.txt");
        final String actual = this.run("symbolic_patterns.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void parserForIdentifiers(final @TempDir Path temp) {
        final String expected = this.loadStringResource("parser_for_identifiers.txt");
        final String actual = this.run("parser_for_identifiers.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void notDefinedTypeInTransformationRule(final @TempDir Path temp) {
        final String message = this.runAndReadErrorMessage(
            "not_defined_type_in_transformation_rule.dsl",
            temp,
            GenerateTest.ANALYZER_EXCERT
        );
        Assertions.assertEquals(
            "not_defined_type_in_transformation_rule.dsl, 25: The resulting node is of type 'BBB' which is not defined",
            message
        );
    }

    @Test
    void nullOnTheRight(final @TempDir Path temp) {
        final String expected = this.loadStringResource("two_identifiers.txt");
        final String actual = this.run("null_on_the_right.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void rightToLeftRule(final @TempDir Path temp) {
        final String expected = this.loadStringResource("right_to_left_rule.txt");
        final String actual = this.run("right_to_left_rule.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void repeatedAndInverted(final @TempDir Path temp) {
        final String expected = this.loadStringResource("repeated_and_inverted.txt");
        final String actual = this.run("repeated_and_inverted.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void optionalAndInverted(final @TempDir Path temp) {
        final String expected = this.loadStringResource("optional_and_inverted.txt");
        final String actual = this.run("optional_and_inverted.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void optionalAndInvertedComplexCase(final @TempDir Path temp) {
        final String expected = this.loadStringResource(
            "optional_and_inverted_complex_case.txt"
        );
        final String actual = this.run("optional_and_inverted_complex_case.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void regularAndInverted(final @TempDir Path temp) {
        final String expected = this.loadStringResource("regular_and_inverted.txt");
        final String actual = this.run("regular_and_inverted.dsl", temp);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Runs the project in code generation mode and compiles all generated files
     *  into a single listing.
     * @param rules Name of the file containing the rules (DSL code)
     * @param dir Temporary folder path
     * @return Generated code
     */
    private String run(final String rules, final Path dir) {
        final Path output = dir.resolve("output");
        final String[] args = {
            "generate",
            String.format("src/test/resources/dsl/%s", rules),
            "--output",
            output.toFile().getAbsolutePath(),
            "--package",
            "org.cqfn.uast.tree",
            "--license",
            "LICENSE.txt",
            "--version",
            "1.0.0",
        };
        Main.main(args);
        Assertions.assertTrue(output.toFile().exists());
        return this.getAllFilesContent(output);
    }

    /**
     * Starts the project in generation mode, but expect the execution to terminate with an error.
     * @param rules Name of the file containing the rules (DSL code)
     * @param dir Temporary folder path
     * @param initiator Name of the module that triggered the exception
     * @return Error message
     */
    private String runAndReadErrorMessage(final String rules, final Path dir,
        final String initiator) {
        String message = "";
        final Path output = dir.resolve("output");
        final String[] args = {
            "generate",
            String.format("src/test/resources/dsl/%s", rules),
            "--output",
            output.toFile().getAbsolutePath(),
            "--package",
            "org.cqfn.uast.tree",
            "--license",
            "LICENSE.txt",
            "--version",
            "1.0.0",
        };
        boolean oops = false;
        try {
            Main.run(args);
        } catch (final BaseException exception) {
            oops = true;
            message = exception.getErrorMessage();
            Assertions.assertEquals(initiator, exception.getInitiator());
        }
        Assertions.assertTrue(oops);
        return message;
    }
}
