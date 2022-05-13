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
package org.uast.astgen.parser;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.rules.Literal;
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

    /**
     * Test parsed literal.
     */
    @Test
    public void parseLiteral() {
        boolean oops = false;
        final String source =
            "IntegerLiteral <- $int$, $String.valueOf(#)$, $Integer.parseInt(#)$;";
        final ProgramParser parser = new ProgramParser(source);
        try {
            final Program program = parser.parse();
            final List<Statement<Literal>> list = program.getLiterals();
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals(
                "green: IntegerLiteral <- $int$, $String.valueOf(#)$, $Integer.parseInt(#)$",
                list.get(0).toString()
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
