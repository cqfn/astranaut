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

package org.cqfn.astranaut.codegen.java;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.rules.Statement;
import org.cqfn.astranaut.utils.FilesReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link OrdinaryNodeGenerator} class.
 *
 * @since 1.0
 */
public class OrdinaryNodeGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing node generation without optional children.
     */
    @Test
    public void testNodeWithoutOptionalChildren() {
        final boolean result = this.testNodeGeneration(
            "Addition <- left@Expression, right@Expression;\nExpression <- Addition | Subtraction",
            "Addition",
            "node_generator.txt"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Testing node generation with optional child.
     */
    @Test
    public void testNodeWithOptionalChild() {
        final boolean result = this.testNodeGeneration(
            "Name <- [composition@Name], last@Identifier;",
            "Name",
            "optional_child.txt"
        );
        Assertions.assertTrue(result);
    }

    /**
     * Testing source code generation for rules that describe nodes.
     * @param source The source string
     * @param type Expected node type
     * @param filename The name of file that contains correct result
     * @return Testing result
     */
    @SuppressWarnings("PMD.CloseResource")
    private boolean testNodeGeneration(final String source,
        final String type, final String filename) {
        final Statement<Node> statement = this.createStatement(source, type);
        final Environment env =
            new TestEnvironment(Collections.singletonList(statement.getRule()));
        final OrdinaryNodeGenerator generator = new OrdinaryNodeGenerator(env, statement);
        final String actual = generator.generate().generate();
        final String expected = this.readTest(filename);
        Assertions.assertEquals(expected, actual);
        return expected.equals(actual);
    }

    /**
     * Creates DSL statement.
     * @param source The source string
     * @param type Expected node type
     * @return DSL statement
     */
    private Statement<Node> createStatement(final String source, final String type) {
        boolean oops = false;
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final List<Statement<Node>> list = program.getNodes();
            for (final Statement<Node> statement : list) {
                if (statement.getRule().getType().equals(type)) {
                    return statement;
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
            result = new FilesReader(OrdinaryNodeGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
