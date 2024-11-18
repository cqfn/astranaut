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
package org.cqfn.astranaut.cli;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cqfn.astranaut.analyzer.Analyzer;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.parser.DslReader;
import org.cqfn.astranaut.parser.ProgramParser;

/**
 * Entry point.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.ProhibitPublicStaticMethods")
public final class Main {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Private constructor.
     */
    private Main() {
    }

    /**
     * Entry point.
     * @param args Program arguments
     */
    public static void main(final String[] args) {
        try {
            Main.run(args);
        } catch (final BaseException exception) {
            LOGGER.log(
                Level.SEVERE,
                String.format(
                    "Exception thrown in the '%s' module\n%s",
                    exception.getInitiator(),
                    exception.getErrorMessage()
                )
            );
        }
    }

    /**
     * Runs parsing command line parameters, and then interpreting or generating code.
     * @param args Program arguments
     * @throws BaseException An exception that occurred while the program was running
     */
    public static void run(final String... args) throws BaseException {
        if (args.length == 0) {
            throw new CommonCliException("Parameters are not specified");
        }
        final Action action;
        if (args[0].equals("generate")) {
            action = new Generate();
        } else {
            throw new CommonCliException(String.format("Unknown action: '%s'", args[0]));
        }
        if (args.length < 2) {
            throw new CommonCliException("A file with a DSL program is not specified");
        }
        final DslReader reader = new DslReader();
        reader.readFile(args[1]);
        final ProgramParser parser = new ProgramParser();
        final Program program = parser.parse(reader);
        final Analyzer analyzer = new Analyzer(program);
        analyzer.analyze();
        action.perform(program, Arrays.asList(args).subList(2, args.length));
    }
}
