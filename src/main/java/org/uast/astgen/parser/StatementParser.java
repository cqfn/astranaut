/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.parser;

import org.uast.astgen.exceptions.BadRuleSyntax;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Statement;

/**
 * Statement parser, processes individual program lines.
 *
 * @since 1.0
 */
public class StatementParser {
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
    public StatementParser(final Program program) {
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
            final Node node = new NodeParser(source).parse();
            this.program.addNodeStmt(new Statement<Node>(node, this.language));
        } else {
            throw BadRuleSyntax.INSTANCE;
        }
    }
}
