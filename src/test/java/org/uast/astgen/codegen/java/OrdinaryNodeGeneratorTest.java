/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.parser.ProgramParser;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.utils.FilesReader;

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
     * Testing source code generation for rules that describe nodes.
     */
    @Test
    @SuppressWarnings("PMD.CloseResource")
    public void testNodeGeneration() {
        final Environment env = new TestEnvironment();
        final Statement<Node> statement = this.createStatement();
        final OrdinaryNodeGenerator generator = new OrdinaryNodeGenerator(env, statement);
        final String actual = generator.generate().generate();
        final String expected = this.readTest("node_generator.txt");
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Creates DSL statement.
     * @return DSL statement
     */
    private Statement<Node> createStatement() {
        boolean oops = false;
        final String source =
            "Addition <- left@Expression, right@Expression;\nExpression <- Addition | Subtraction";
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final List<Statement<Node>> list = program.getNodes();
            for (final Statement<Node> statement : list) {
                if (statement.getRule().getType().equals("Addition")) {
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
