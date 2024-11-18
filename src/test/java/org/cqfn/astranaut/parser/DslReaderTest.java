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
package org.cqfn.astranaut.parser;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests covering {@link DslReader} class.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.CloseResource")
class DslReaderTest {
    @Test
    void parseString() {
        boolean oops = false;
        try {
            final String code =
                "This <- 0;\nAddition\n<-\nleft@Expression, right@Expression;Expression <- This | Addition";
            final DslReader reader = new DslReader();
            reader.setSourceCode(code);
            Statement stmt = reader.getStatement();
            Assertions.assertEquals("1: This <- 0", stmt.toString());
            stmt = reader.getStatement();
            Assertions.assertEquals(
                "2-4: Addition <- left@Expression, right@Expression",
                stmt.toString()
            );
            stmt = reader.getStatement();
            Assertions.assertEquals("4: Expression <- This | Addition", stmt.toString());
            stmt = reader.getStatement();
            Assertions.assertNull(stmt);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void readFile() {
        final DslReader reader = new DslReader();
        boolean oops = false;
        try {
            reader.readFile("src/test/resources/dsl/simple.dsl");
            Statement stmt = reader.getStatement();
            Assertions.assertEquals("simple.dsl, 25: This <- 0", stmt.toString());
            stmt = reader.getStatement();
            Assertions.assertEquals(
                "simple.dsl, 27-29: Addition <- left@Expression, right@Expression",
                stmt.toString()
            );
            stmt = reader.getStatement();
            Assertions.assertEquals(
                "simple.dsl, 29: Expression <- This | Addition",
                stmt.toString()
            );
            stmt = reader.getStatement();
            Assertions.assertNull(stmt);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void parseNothing() {
        final DslReader reader = new DslReader();
        reader.setSourceCode("");
        boolean oops = false;
        try {
            final Statement stmt = reader.getStatement();
            Assertions.assertNull(stmt);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void readNonexistentFile() {
        final DslReader reader = new DslReader();
        boolean oops = false;
        try {
            reader.readFile("file.that.does.not.exist.dsl");
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Parser", exception.getInitiator());
            Assertions.assertEquals(
                "Can't read 'file.that.does.not.exist.dsl'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void readFileContainingImports() {
        final DslReader reader = new DslReader();
        boolean oops = false;
        try {
            reader.readFile("src/test/resources/dsl/method_calls.dsl");
            final Set<String> expected = new TreeSet<>(
                Arrays.asList(
                    "IntegerLiteral",
                    "StringLiteral",
                    "Literal",
                    "Addition",
                    "Subtraction",
                    "BinaryOperation",
                    "Expression",
                    "ExpressionList",
                    "Identifier",
                    "MethodCall"
                )
            );
            Statement stmt = reader.getStatement();
            while (stmt != null) {
                for (final String name : expected) {
                    if (stmt.getCode().startsWith(name)) {
                        expected.remove(name);
                        break;
                    }
                }
                stmt = reader.getStatement();
            }
            Assertions.assertTrue(expected.isEmpty());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    @Test
    void readFileWhereImportHasAbsolutePath(final @TempDir Path dir) {
        final String imported = dir.resolve("imported.dsl").toAbsolutePath().toString();
        FilesWriter writer = new FilesWriter(imported);
        boolean okay = writer.writeStringNoExcept("D <- E, F;");
        Assertions.assertTrue(okay);
        final String main = dir.resolve("main.dsl").toAbsolutePath().toString();
        writer = new FilesWriter(main);
        okay = writer.writeStringNoExcept(
            String.format("import %s;\nA <- B, C;", imported)
        );
        Assertions.assertTrue(okay);
        final DslReader reader = new DslReader();
        boolean oops = false;
        try {
            reader.readFile(main);
            final Set<String> expected = new TreeSet<>(Arrays.asList("A", "D"));
            Statement stmt = reader.getStatement();
            while (stmt != null) {
                for (final String name : expected) {
                    if (stmt.getCode().startsWith(name)) {
                        expected.remove(name);
                        break;
                    }
                }
                stmt = reader.getStatement();
            }
            Assertions.assertTrue(expected.isEmpty());
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
