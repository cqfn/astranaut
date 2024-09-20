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
package org.cqfn.astranaut.interpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.cqfn.astranaut.Main;
import org.cqfn.astranaut.core.base.CoreException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.DestinationNotSpecified;
import org.cqfn.astranaut.exceptions.InterpreterException;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test that covers {@link Interpreter} class.
 *
 * @since 0.1.5
 */
class InterpreterTest {
    /**
     * First test.
     * @param temp A temporary directory
     */
    @Test
    void firstTest(@TempDir final Path temp) {
        final boolean result = this.test("test_0", temp);
        Assertions.assertTrue(result);
    }

    /**
     * Testing holes with ellipsis.
     * @param temp A temporary directory
     */
    @Test
    void ellipsisTest(@TempDir final Path temp) {
        final boolean result = this.test("test_1", temp);
        Assertions.assertTrue(result);
    }

    /**
     * Testing removing parent of subtree.
     * @param temp A temporary directory
     */
    @Test
    void removeParentTest(@TempDir final Path temp) {
        final boolean result = this.test("test_2", temp);
        Assertions.assertTrue(result);
    }

    /**
     * Testing holes with a node type.
     * @param temp A temporary directory
     */
    @Test
    void typedHoleTest(@TempDir final Path temp) {
        final boolean result = this.test("test_3", temp);
        Assertions.assertTrue(result);
    }

    /**
     * Testing holes with a node type, that is not exists.
     * @param temp A temporary directory
     */
    @Test
    void typedHoleNotExistsTest(@TempDir final Path temp) {
        final boolean result = this.test("test_4", temp);
        Assertions.assertTrue(result);
    }

    /**
     * Testing running interpreter without a destination specified.
     */
    @Test
    void notSpecifiedDestinationTest() {
        final Interpreter interpreter = this.createInterpreter(
            "rules.dsl",
            null
        );
        Exception result = null;
        try {
            interpreter.run();
        } catch (final InterpreterException exception) {
            result = exception;
        }
        Assertions.assertNotNull(result);
        Assertions.assertEquals(DestinationNotSpecified.INSTANCE, result);
    }

    /**
     * Testing the exception that occurs when the interpreter cannot read
     * the specified JSON file with a syntax tree, because the path to file is wrong.
     */
    @Test
    void cannotReadJsonFileTest() {
        final String path = "wrong_path.json";
        final Interpreter interpreter = this.createInterpreter(
            path,
            new File("result.json")
        );
        String result = "";
        try {
            interpreter.run();
        } catch (final InterpreterException exception) {
            result = exception.getErrorMessage();
        }
        Assertions.assertNotNull(result);
        final String message = String.format(
            "Could not read the file that contains source syntax tree: %s", path
        );
        Assertions.assertEquals(message, result);
    }

    /**
     * Testing the exception that occurs when the interpreter cannot read
     * the specified JSON file with a syntax tree, because the path to file is wrong.
     */
    @Test
    void cannotWriteToJsonFileTest() {
        final String path = File.separator;
        final Interpreter interpreter = this.createInterpreter(
            "src/test/resources/interpreter/test_1_source_tree.json",
            new File(path)
        );
        String result = "";
        try {
            interpreter.run();
        } catch (final InterpreterException exception) {
            result = exception.getErrorMessage();
        }
        Assertions.assertNotNull(result);
        final String message = String.format(
            "Could not write file: '%s'", path
        );
        Assertions.assertEquals(message, result);
    }

    /**
     * Performs the test.
     * @param prefix The prefix of names of files that contains rules and syntax trees
     * @param temp A temporary directory
     * @return Testing result, {@code true} if success
     */
    private boolean test(final String prefix, @TempDir final Path temp) {
        final String path = "src/test/resources/interpreter/".concat(prefix);
        final Path dst = temp.resolve("result.json");
        final String[] args = {
            "--action",
            "convert",
            "--rules",
            path.concat("_rules.dsl"),
            "--source",
            path.concat("_source_tree.json"),
            "--destination",
            dst.toString(),
        };
        boolean oops = false;
        try {
            Main.main(args);
        } catch (final CoreException ignored) {
            oops = true;
        }
        boolean result = false;
        try {
            final String expected = new FilesReader(path.concat("_result.json")).readAsString();
            final String actual = new FilesReader(dst.toString()).readAsString();
            result = expected.equals(actual);
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }

    /**
     * Creates an interpreter instance for testing exceptions thrown by the interpreter.
     * @param source The path to the source JSON file
     * @param target The target JSON file
     * @return New interpreter instance
     */
    private Interpreter createInterpreter(final String source, final File target) {
        boolean oops = false;
        Program program = null;
        try {
            final ProgramParser parser = new ProgramParser("X(Y<\"2\">) -> Z");
            program = parser.parse();
        } catch (final CoreException ignore) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertNotNull(program);
        return new Interpreter(
            new File(source),
            target,
            program
        );
    }
}
