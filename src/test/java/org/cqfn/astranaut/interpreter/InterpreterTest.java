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
package org.cqfn.astranaut.interpreter;

import java.io.IOException;
import java.nio.file.Path;
import org.cqfn.astranaut.Main;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test that covers {@link Interpreter} class.
 *
 * @since 0.1.5
 */
public class InterpreterTest {
    /**
     * First test.
     * @param temp A temporary directory
     */
    @Test
    public void firstTest(@TempDir final Path temp) {
        final boolean result = this.test("test_0", temp);
        Assertions.assertTrue(result);
    }

    /**
     * Testing holes with ellipsis.
     * @param temp A temporary directory
     */
    @Test
    public void ellipsisTest(@TempDir final Path temp) {
        final boolean result = this.test("test_1", temp);
        Assertions.assertTrue(result);
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
        } catch (final BaseException ignored) {
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
}
