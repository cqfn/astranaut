/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils;

import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.uast.astgen.Main;
import org.uast.astgen.utils.cli.FileConverter;

/**
 * Test for {@link FileConverter} class exception cases.
 *
 * @since 1.0
 */
public class FileConverterTest {
    /**
     * The action option as an argument example.
     */
    private static final String ACTION = "--action";

    /**
     * The 'generate' option value as an argument example.
     */
    private static final String GENERATE = "generate";

    /**
     * The rules option as an argument example.
     */
    private static final String RULES = "--rules";

    /**
     * Test passing the {@code --rules} option to main() with file
     * which has an irrelevant extension.
     * @param source A temporary directory
     */
    @Test
    public void testWrongFileExtension(@TempDir final Path source) throws IOException {
        final Path file = source.resolve("example.java");
        final List<String> lines = Collections.singletonList("class A{}");
        Files.write(file, lines);
        final String[] example = {
            FileConverterTest.ACTION,
            FileConverterTest.GENERATE,
            FileConverterTest.RULES,
            file.toString(),
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException exc) {
            caught = true;
        }
        Assertions.assertTrue(caught);
    }

    /**
     * Test passing the {@code --rules} option to main() with file
     * which does not exist.
     */
    @Test
    public void testNotExistingFile() throws IOException {
        final String[] example = {
            FileConverterTest.ACTION,
            FileConverterTest.GENERATE,
            FileConverterTest.RULES,
            "some/file.txt",
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException exc) {
            caught = true;
        }
        Assertions.assertTrue(caught);
    }
}
