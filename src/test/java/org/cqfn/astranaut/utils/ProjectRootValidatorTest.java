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

package org.cqfn.astranaut.utils;

import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.Main;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.utils.cli.ProjectRootValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link ProjectRootValidator} class.
 *
 * @since 1.0
 */
public class ProjectRootValidatorTest {
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
     * The name of the option for a root of the target project.
     */
    private static final String ROOT = "--output";

    /**
     * The test option as an argument example.
     */
    private static final String TEST = "--test";

    /**
     * Test passing the {@code --rules} option to main()
     * with the {@code --root} option and a valid
     * non-existing path as a parameter.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionNoException(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ACTION,
            ProjectRootValidatorTest.GENERATE,
            ProjectRootValidatorTest.RULES,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            "test/root",
            ProjectRootValidatorTest.TEST,
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException | BaseException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing the {@code --rules} option to main()
     * with the {@code --root} option and an invalid parameter.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionWithException(@TempDir final Path source) throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ACTION,
            ProjectRootValidatorTest.GENERATE,
            ProjectRootValidatorTest.RULES,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            "test/ro\0ot",
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException | BaseException exc) {
            caught = true;
        }
        Assertions.assertTrue(caught);
    }

    /**
     * Test passing the {@code --rules} option to main()
     * with the {@code --root} option and a valid
     * already existing path  as a parameter.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionWithExistingPath(@TempDir final Path source)
        throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ACTION,
            ProjectRootValidatorTest.GENERATE,
            ProjectRootValidatorTest.RULES,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            source.toString(),
            ProjectRootValidatorTest.TEST,
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException | BaseException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing the {@code --rules} option to main()
     * with the {@code --root} option having no parameter before
     * the next option.
     * @param source A temporary directory
     */
    @Test
    public void testProjectRootOptionWithoutParameter(@TempDir final Path source)
        throws IOException {
        final Path file = this.createTempTxtFile(source);
        final String[] example = {
            ProjectRootValidatorTest.ACTION,
            ProjectRootValidatorTest.GENERATE,
            ProjectRootValidatorTest.RULES,
            file.toString(),
            ProjectRootValidatorTest.ROOT,
            "-p",
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException | BaseException exc) {
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
