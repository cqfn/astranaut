/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.parser;

import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.exceptions.ExceptionWithLineNumber;
import org.uast.astgen.exceptions.ParserException;
import org.uast.astgen.rules.Program;

/**
 * Parses the whole DSL program.
 *
 * @since 1.0
 */
public class ProgramParser {
    /**
     * Source string.
     */
    private final String source;

    /**
     * Constructor.
     * @param source The source string.
     */
    public ProgramParser(final String source) {
        this.source = source;
    }

    /**
     * Parses the whole DSL program.
     * @return Parsed program.
     * @throws BaseException If source can't be parsed
     */
    public Program parse() throws BaseException {
        final String[] lines = this.source.split(";");
        final Program program = new Program();
        final StatementParser parser = new StatementParser(program);
        int number = 1;
        for (final String line : lines) {
            try {
                parser.parse(line);
            } catch (final ParserException error) {
                throw new ExceptionWithLineNumber(error, number);
            }
            number = number + 1;
        }
        return program;
    }
}
