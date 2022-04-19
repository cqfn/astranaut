/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.parser.ProgramParser;
import org.uast.astgen.rules.Literal;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.utils.FilesReader;

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
