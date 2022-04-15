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

/**
 * Test for {@link ProjectRootValidator} class.
 *
 * @since 1.0
 */
public class ProjectRootValidatorTest {
    /**
     * The generate option as an argument example.
     */
    private static final String ARG = "--generate";

    /**
     * The name of the option for a root of the target project.
     */
    private static final String ROOT = "--output";

    /**
     * Test passing the {@code --generate} option to main()
     * with the {@code --root} option and a valid
     * non-existing path as a parameter.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionNoException(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ARG,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            "test/root",
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing the {@code --generate} option to main()
     * with the {@code --root} option and an invalid parameter.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionWithException(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ARG,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            "test/ro\0ot",
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
     * Test passing the {@code --generate} option to main()
     * with the {@code --root} option and a valid
     * already existing path  as a parameter.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionWithExistingPath(@TempDir final Path source)
        throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ARG,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            source.toString(),
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing the {@code --generate} option to main()
     * with the {@code --root} option having no parameter before
     * the next option.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionWithoutParameter(@TempDir final Path source)
        throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ARG,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            "-p",
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
     * Creates a temporary directory to test passing files in CLI options.
     * @param source A temporary directory
     * @return The path to a temporary file
     * @throws IOException If fails to create a temporary file
     */
    private Path createTempTxtFile(@TempDir final Path source) throws IOException {
        final Path file = source.resolve("example.txt");
        final List<String> lines = Collections.singletonList("Addition<-Expression, Expression;");
        Files.write(file, lines);
        return file;
    }
}
