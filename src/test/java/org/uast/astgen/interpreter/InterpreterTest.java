/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.uast.astgen.Main;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.utils.FilesReader;

/**
 * Test that covers {@link Interpreter} class.
 *
 * @since 1.0
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
