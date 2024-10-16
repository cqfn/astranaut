/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

package org.cqfn.astranaut.parser;

import org.cqfn.astranaut.exceptions.BadRuleSyntax;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Literal;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.rules.Transformation;
import org.cqfn.astranaut.rules.Vertex;

/**
 * Instruction parser, processes individual program lines.
 *
 * @since 0.1.5
 */
public class InstructionParser {
    /**
     * The program, i.e. set of DSL rules with addition data.
     */
    private final Program program;

    /**
     * Current language.
     */
    private String language;

    /**
     * Constructor.
     * @param program The program.
     */
    public InstructionParser(final Program program) {
        this.program = program;
        this.language = "";
    }

    /**
     * Parses line of code.
     * @param source The source line
     * @throws ParserException If line can't be parsed.
     */
    public void parse(final String source) throws ParserException {
        final String code;
        final int separator = source.indexOf(':');
        if (separator < 0) {
            code = source;
        } else {
            this.language = source.substring(0, separator).trim();
            code = source.substring(separator + 1).trim();
        }
        if (!code.isEmpty()) {
            this.parseDsl(code);
        }
    }

    /**
     * Parses line of DSL code.
     * @param source The source line
     * @throws ParserException If line can't be parsed.
     */
    protected void parseDsl(final String source) throws ParserException {
        if (source.contains("<-")) {
            if (source.contains("$")) {
                final Literal literal = new LiteralParser(source).parse();
                this.program.addLiteralInstruction(
                    new Instruction<Literal>(literal, this.language)
                );
                this.program.addVertexInstruction(
                    new Instruction<Vertex>(literal, this.language)
                );
            } else {
                final Node node = new NodeParser(source).parse();
                this.program.addNodeInstruction(new Instruction<Node>(node, this.language));
                this.program.addVertexInstruction(new Instruction<Vertex>(node, this.language));
            }
        } else if (source.contains("->")) {
            final Transformation rule = new TransformationParser(source).parse();
            this.program.addTransformInstruction(
                new Instruction<Transformation>(rule, this.language)
            );
        } else {
            throw BadRuleSyntax.INSTANCE;
        }
    }
}
