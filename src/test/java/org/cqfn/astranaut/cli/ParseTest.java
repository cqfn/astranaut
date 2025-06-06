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

import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests checking parsing with different rule sets.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
class ParseTest extends EndToEndTest {
    @Test
    void allLongParameters(final @TempDir Path temp) {
        final Path ast = temp.resolve("ast.json");
        final Path image = temp.resolve("image.png");
        final String[] args = {
            "parse",
            "src/test/resources/dsl/identifiers_numbers_and_operators.dsl",
            "--ignored",
            "--source",
            "src/test/resources/sources/two_additions.txt",
            "--language",
            "common",
            "--ast",
            ast.toFile().getAbsolutePath(),
            "--image",
            image.toFile().getAbsolutePath(),
        };
        Main.main(args);
        Assertions.assertTrue(ast.toFile().exists());
        Assertions.assertTrue(image.toFile().exists());
    }

    @Test
    void allShortParameters(final @TempDir Path temp) {
        final Path ast = temp.resolve("ast.json");
        final Path image = temp.resolve("image.png");
        final String[] args = {
            "parse",
            "src/test/resources/dsl/identifiers_numbers_and_operators.dsl",
            "-s",
            "src/test/resources/sources/two_additions.txt",
            "-l",
            "common",
            "-t",
            ast.toFile().getAbsolutePath(),
            "-i",
            image.toFile().getAbsolutePath(),
        };
        Main.main(args);
        Assertions.assertTrue(ast.toFile().exists());
        Assertions.assertTrue(image.toFile().exists());
    }

    @Test
    void twoAdditions(final @TempDir Path temp) {
        final String actual = this.run(
            "identifiers_numbers_and_operators.dsl",
            "two_additions.txt",
            temp
        );
        final String expected = this.loadStringResource("two_additions.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nullOnTheRight(final @TempDir Path temp) {
        final String actual = this.run(
            "null_on_the_right.dsl",
            "two_identifiers.txt",
            temp
        );
        final String expected = this.loadStringResource("two_identifiers.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void chainedAssignment(final @TempDir Path temp) {
        final String actual = this.run(
            "right_to_left_rule.dsl",
            "chained_assignment.txt",
            temp
        );
        final String expected = this.loadStringResource("chained_assignment.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void repeatedAndInverted(final @TempDir Path temp) {
        final String actual = this.run(
            "repeated_and_inverted.dsl",
            "identifiers_and_string.txt",
            temp
        );
        final String expected = this.loadStringResource("identifiers_and_string.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void optionalAndInverted(final @TempDir Path temp) {
        final String actual = this.run(
            "optional_and_inverted.dsl",
            "identifiers_and_text.txt",
            temp
        );
        final String expected = this.loadStringResource("identifiers_and_text.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void regularAndInverted(final @TempDir Path temp) {
        final String actual = this.run(
            "regular_and_inverted.dsl",
            "three_symbols.txt",
            temp
        );
        final String expected = this.loadStringResource("three_symbols.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void orExpression(final @TempDir Path temp) {
        final String actual = this.run(
            "or_expression.dsl",
            "identifier_and_number.txt",
            temp
        );
        final String expected = this.loadStringResource("letters_and_digits.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void andExpression(final @TempDir Path temp) {
        final String actual = this.run(
            "and_expression.dsl",
            "identifier_and_number.txt",
            temp
        );
        final String expected = this.loadStringResource("letters_and_digits.json");
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Runs the project in parsing mode and reads the generated file.
     * @param rules Name of the file containing the rules (DSL code)
     * @param source Name of the file containing the source code to be parsed
     * @param dir Temporary folder path
     * @return Generated syntax tree
     */
    private String run(final String rules, final String source, final Path dir) {
        final Path ast = dir.resolve("ast.json");
        final String[] args = {
            "parse",
            String.format("src/test/resources/dsl/%s", rules),
            "--source",
            String.format("src/test/resources/sources/%s", source),
            "--ast",
            ast.toFile().getAbsolutePath(),
        };
        Main.main(args);
        Assertions.assertTrue(ast.toFile().exists());
        return this.getAllFilesContent(ast);
    }
}
