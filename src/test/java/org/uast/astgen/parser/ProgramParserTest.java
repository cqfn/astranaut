/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Rule;
import org.uast.astgen.rules.Statement;

/**
 * Test for {@link ProgramParser} class.
 *
 * @since 1.0
 */
public class ProgramParserTest {
    /**
     * Test parsed 2 statements.
     */
    @Test
    public void parseTwoStatements() {
        boolean oops = false;
        final String source =
            "Addition<-Expression, Expression;\njava:\nSynchronized<-Expression, StatementBlock;";
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final List<Statement<Rule>> list = program.getAllRules();
            Assertions.assertEquals(2, list.size());
            Assertions.assertEquals(
                "green: Addition <- Expression, Expression",
                list.get(0).toString()
            );
            Assertions.assertEquals(
                "java: Synchronized <- Expression, StatementBlock",
                list.get(1).toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Test parsed code with error.
     */
    @Test
    public void parseCodeWithError() {
        boolean oops = false;
        final String source =
            "Addition <- Expression, Expression;\nexpression <- Addition | Subtraction";
        final ProgramParser parser = new ProgramParser(source);
        try {
            parser.parse();
        } catch (final BaseException error) {
            final String message = error.getErrorMessage();
            Assertions.assertEquals(
                "2: Node names must start with a capital letter: 'Expression'",
                message
            );
            oops = true;
        }
        Assertions.assertTrue(oops);
    }
}
