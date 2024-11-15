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
    void readBadDsl(final @TempDir Path temp) {
        final String message = this.runAndReadErrorMessage("bad.dsl", temp);
        Assertions.assertEquals(
            "bad.dsl, 26: The rule does not contain a separator",
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
     * @return Error message
     */
    private String runAndReadErrorMessage(final String rules, final Path dir) {
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
        }
        Assertions.assertTrue(oops);
        return message;
    }
}
