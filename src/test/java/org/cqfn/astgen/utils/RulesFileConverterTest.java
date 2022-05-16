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
package org.cqfn.astgen.utils;

import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.cqfn.astgen.Main;
import org.cqfn.astgen.exceptions.BaseException;
import org.cqfn.astgen.utils.cli.RulesFileConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link RulesFileConverter} class exception cases.
 *
 * @since 1.0
 */
public class RulesFileConverterTest {
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
            RulesFileConverterTest.ACTION,
            RulesFileConverterTest.GENERATE,
            RulesFileConverterTest.RULES,
            file.toString(),
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
     * Test passing the {@code --rules} option to main() with file
     * which does not exist.
     */
    @Test
    public void testNotExistingFile() throws IOException {
        final String[] example = {
            RulesFileConverterTest.ACTION,
            RulesFileConverterTest.GENERATE,
            RulesFileConverterTest.RULES,
            "some/file.txt",
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final ParameterException | BaseException exc) {
            caught = true;
        }
        Assertions.assertTrue(caught);
    }
}
