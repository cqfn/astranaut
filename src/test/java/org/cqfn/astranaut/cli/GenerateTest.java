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

import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests checking file generation for different rule sets.
 * @since 1.0.0
 */
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
}
