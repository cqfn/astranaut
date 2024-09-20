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
package org.cqfn.astranaut.codegen.java;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import org.cqfn.astranaut.analyzer.EnvironmentPreparator;
import org.cqfn.astranaut.core.base.CoreException;
import org.cqfn.astranaut.exceptions.GeneratorCouldNotWriteFile;
import org.cqfn.astranaut.exceptions.GeneratorException;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link ProgramGenerator} class.
 *
 * @since 0.2.6
 */
class ProgramGeneratorTest {
    /**
     * Test generation of a program.
     * @param temp A temporary directory
     */
    @Test
    void testProgramGeneration(@TempDir final Path temp) {
        final ProgramGenerator generator = this.createProgramGenerator(temp.toString());
        boolean oops = false;
        try {
            generator.generate();
        } catch (final GeneratorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test generation of a program with incorrect path for generated files.
     */
    @Test
    void testGenerationException() {
        final ProgramGenerator generator = this.createProgramGenerator(File.separator);
        Exception thrown = null;
        try {
            generator.generate();
        } catch (final GeneratorException exception) {
            thrown = exception;
        }
        Assertions.assertNotNull(thrown);
        Assertions.assertTrue(thrown instanceof GeneratorCouldNotWriteFile);
    }

    private ProgramGenerator createProgramGenerator(final String path) {
        boolean oops = false;
        Program program = null;
        try {
            final ProgramParser parser = new ProgramParser(
                "StringLiteral <- $String$, $#$, $#$;"
            );
            program = parser.parse();
        } catch (final CoreException ignore) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        Assertions.assertNotNull(program);
        oops = false;
        Map<String, Environment> env = null;
        try {
            env =
                new EnvironmentPreparator(program, new TestEnvironment()).prepare();
        } catch (final GeneratorException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return new ProgramGenerator(path, program, env);
    }
}
