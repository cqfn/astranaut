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
import org.uast.astgen.utils.cli.PackageValidator;

/**
 * Test for {@link PackageValidator} class.
 *
 * @since 1.0
 */
public class PackageValidatorTest {
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
     * The test option as an argument example.
     */
    private static final String TEST = "--test";

    /**
     * The name of the option for a package name of generated files.
     */
    private static final String PCG = "--package";

    /**
     * Test passing the {@code --rules} option to main()
     * with the {@code --package} option and a valid package name
     * as a parameter.
     * @param source A temporary directory
     */
    @Test
    public void testPackageOptionNoException(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            PackageValidatorTest.ACTION,
            PackageValidatorTest.GENERATE,
            PackageValidatorTest.RULES,
            file.toString(),
            PackageValidatorTest.PCG,
            "org.uast",
            PackageValidatorTest.TEST,
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
     * Test passing the {@code --rules} option to main()
     * with the {@code --package} option and an invalid package name
     * as a parameter.
     * @param source A temporary directory
     */
    @Test
    public void testPackageOptionWithException(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            PackageValidatorTest.ACTION,
            PackageValidatorTest.GENERATE,
            PackageValidatorTest.RULES,
            file.toString(),
            PackageValidatorTest.PCG,
            "org/uast",
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
     * Test passing the {@code --rules} option to main()
     * with the {@code --package} option having no parameter before
     * the next option.
     * @param source A temporary directory
     */
    @Test
    public void testPackageOptionWithoutParameter(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            PackageValidatorTest.ACTION,
            PackageValidatorTest.GENERATE,
            PackageValidatorTest.RULES,
            file.toString(),
            PackageValidatorTest.PCG,
            "-r",
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
