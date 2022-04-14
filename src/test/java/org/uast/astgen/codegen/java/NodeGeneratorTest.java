/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.io.IOException;
import java.util.Collections;
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
 * Tests for {@link NodeGenerator} class.
 *
 * @since 1.0
 */
public class NodeGeneratorTest {
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
        final NodeGenerator generator = new NodeGenerator(env);
        final Statement<Node> statement = this.createStatement();
        final String actual = generator.generate(statement).generate();
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
            result = new FilesReader(NodeGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }

    /**
     * Environment for test purposes.
     *
     * @since 1.0
     */
    private static final class TestEnvironment implements Environment {
        /**
         * The license.
         */
        private final License license;

        /**
         * Constructor.
         */
        TestEnvironment() {
            this.license = new License("LICENSE_header.txt");
        }

        @Override
        public License getLicense() {
            return this.license;
        }

        @Override
        public String getRootPackage() {
            return "org.uast.example";
        }

        @Override
        public String getBasePackage() {
            return "org.uast.uast.base";
        }

        @Override
        public List<String> getHierarchy(final String name) {
            return Collections.singletonList(name);
        }
    }
}