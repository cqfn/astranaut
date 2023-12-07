/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link OrdinaryNodeGenerator} class.
 *
 * @since 0.1.5
 */
class AbstractNodeGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing node generation with specified ASTranaut version.
     */
    @Test
    void testAbstractNodeWithGeneratorVersion() {
        final String source =
            "Expression <- Binary | Ternary; Binary <- Expression, Expression; Ternary <- Expression, Expression, Expression";
        final String type = "Expression";
        final String filename = "abstract_node_with_generator_version.txt";
        final Instruction<Node> instruction = this.createInstruction(source, type);
        final TestEnvironment env =
            new TestEnvironment(Collections.singletonList(instruction.getRule()));
        env.addGeneratorVersion();
        final AbstractNodeGenerator generator = new AbstractNodeGenerator(env, instruction);
        final String actual = generator.generate().generate();
        final String expected = this.readTest(filename)
            .replace("{av}", org.cqfn.astranaut.Common.VERSION)
            .replace("{cv}", org.cqfn.astranaut.core.Common.VERSION);
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Creates DSL instruction.
     * @param source The source string
     * @param type Expected node type
     * @return DSL instruction
     */
    private Instruction<Node> createInstruction(final String source, final String type) {
        boolean oops = false;
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final List<Instruction<Node>> list = program.getNodes();
            for (final Instruction<Node> instruction : list) {
                if (instruction.getRule().getType().equals(type)) {
                    return instruction;
                }
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
            result = new FilesReader(AbstractNodeGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
