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
import org.uast.astgen.rules.Program;
import org.uast.astgen.utils.FilesReader;

/**
 * Tests for {@link FactoryGenerator} class.
 *
 * @since 1.0
 */
public class FactoryGeneratorTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/codegen/java/";

    /**
     * Testing source code generation for rules that describe nodes.
     */
    @Test
    public void testFactoryGeneration() {
        boolean oops = false;
        try {
            final String source = this.readTest("factory_generator_source.txt");
            final ProgramParser parser = new ProgramParser(source);
            final Program program = parser.parse();
            final Environment env = new TestEnvironment();
            final FactoryGenerator generator = new FactoryGenerator(env, program, "c");
            final CompilationUnit unit = generator.generate();
            final String actual = unit.generate();
            final String expected = this.readTest("factory_generator_expected.txt");
            Assertions.assertEquals(expected, actual);
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
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
            result = new FilesReader(FactoryGeneratorTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
