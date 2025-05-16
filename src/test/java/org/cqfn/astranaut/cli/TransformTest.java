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
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests checking AST transformations with different rule sets.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class TransformTest extends EndToEndTest {
    @Test
    void twoAdditions(final @TempDir Path temp) {
        final String actual = this.run(
            "identifiers_numbers_and_operators.dsl",
            "correct_and_strange_chars.json",
            temp
        );
        final String expected = this.loadStringResource("symbol_and_strange_char.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void readingNonExistingFile() {
        final String[] args = {
            "transform",
            "src/test/resources/dsl/identifiers_numbers_and_operators.dsl",
            "--source",
            "file.that.does.not.exist",
        };
        Assertions.assertThrows(BaseException.class, () -> Main.run(args));
    }

    @Test
    void wrongGraphFileExtension(final @TempDir Path temp) {
        final Path image = temp.resolve("image.txt");
        final String[] args = {
            "transform",
            "src/test/resources/dsl/identifiers_numbers_and_operators.dsl",
            "--source",
            "src/test/resources/sources/correct_and_strange_chars.json",
            "--image",
            image.toFile().getAbsolutePath(),
        };
        Assertions.assertThrows(BaseException.class, () -> Main.run(args));
    }

    @Test
    void writeProtectedGraphFile(final @TempDir Path temp) {
        final Path output = temp.resolve("output");
        this.createWriteProtectedFolder(output);
        final Path ast = temp.resolve("output/ast.json");
        final String[] args = {
            "transform",
            "src/test/resources/dsl/identifiers_numbers_and_operators.dsl",
            "-s",
            "src/test/resources/sources/correct_and_strange_chars.json",
            "-t",
            ast.toFile().getAbsolutePath(),
        };
        Assertions.assertThrows(BaseException.class, () -> Main.run(args));
        this.clearWriteProtectedFlag(output);
    }

    @Test
    void writeProtectedImageFile(final @TempDir Path temp) {
        final Path output = temp.resolve("output");
        this.createWriteProtectedFolder(output);
        final Path image = temp.resolve("output/image.png");
        final String[] args = {
            "transform",
            "src/test/resources/dsl/identifiers_numbers_and_operators.dsl",
            "--source",
            "src/test/resources/sources/correct_and_strange_chars.json",
            "--image",
            image.toFile().getAbsolutePath(),
        };
        Assertions.assertThrows(BaseException.class, () -> Main.run(args));
        this.clearWriteProtectedFlag(output);
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
            "transform",
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
