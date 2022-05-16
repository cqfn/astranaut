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

package org.cqfn.astgen.codegen.java;

import java.io.IOException;
import org.cqfn.astgen.exceptions.BaseException;
import org.cqfn.astgen.parser.ProgramParser;
import org.cqfn.astgen.rules.Literal;
import org.cqfn.astgen.rules.Program;
import org.cqfn.astgen.rules.Statement;
import org.cqfn.astgen.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LiteralGenerator} class.
 *
 * @since 1.0
 */
public class LiteralGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing source code generation for rules that describe literals.
     */
    @Test
    @SuppressWarnings("PMD.CloseResource")
    public void testLiteralGeneration() {
        final Environment env = new TestEnvironment();
        final Statement<Literal> statement = this.createStatement();
        final LiteralGenerator generator = new LiteralGenerator(env, statement);
        final String actual = generator.generate().generate();
        final String expected = this.readTest("literal_generator.txt");
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Creates DSL statement.
     * @return DSL statement
     */
    private Statement<Literal> createStatement() {
        boolean oops = false;
        final String source =
            "IntegerLiteral <- $int$, $String.valueOf(#)$, $Integer.parseInt(#)$, $Exception$";
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            if (!program.getLiterals().isEmpty()) {
                return program.getLiterals().get(0);
            }
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        throw new IllegalStateException();
    }

    /**
     * Reads test source from the file.
     * @param name The file name
     * @return Test source
     */
    private String readTest(final String name) {
        String result = "";
        boolean oops = false;
        try {
            result = new FilesReader(LiteralGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
