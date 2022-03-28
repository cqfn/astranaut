/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Rule;
import org.uast.astgen.rules.Statement;

/**
 * Test for {@link StatementParser} class.
 *
 * @since 1.0
 */
public class StatementParserTest {
    /**
     * Test parsed 2 statements.
     */
    @Test
    public void parseTwoStatements() {
        boolean oops = false;
        final Program program = new Program();
        final StatementParser parser = new StatementParser(program);
        try {
            parser.parse("Addition <- Expression, Expression");
            parser.parse("java:");
            parser.parse("Synchronized <- Expression, StatementBlock");
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
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
