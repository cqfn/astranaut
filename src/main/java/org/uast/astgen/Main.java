/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.parser.ProgramParser;
import org.uast.astgen.rules.Program;
import org.uast.astgen.utils.FileConverter;
import org.uast.astgen.utils.FilesReader;

/**
 * Main class.
 *
 * @since 1.0
 */
public final class Main {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * The source file.
     */
    @Parameter(
        names = { "--generate", "-g" },
        converter = FileConverter.class,
        required = true,
        description = "Txt file with DSL descriptions"
    )
    private File source;

    /**
     * The help option.
     */
    @SuppressWarnings("PMD.ImmutableField")
    @Parameter(names = "--help", help = true)
    private boolean help;

    /**
     * Private constructor.
     */
    private Main() {
        this.help = false;
    }

    /**
     * The main function. Parses the command line.
     * @param args The command-line arguments
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        final Main main = new Main();
        final JCommander jcr = JCommander.newBuilder()
            .addObject(main)
            .build();
        jcr.parse(args);
        if (main.help) {
            jcr.usage();
            return;
        }
        main.run();
    }

    /**
     * Runs actions.
     * @throws IOException If fails
     */
    private void run() throws IOException {
        final String code = new FilesReader(this.source.getPath()).readAsString();
        final ProgramParser parser = new ProgramParser(code);
        try {
            final Program program = parser.parse();
            final StringBuilder result = new StringBuilder();
            result
                .append(program.getAllRules().size())
                .append(" rules parsed");
            LOG.info(result.toString());
        } catch (final BaseException exc) {
            LOG.severe(exc.getErrorMessage());
        }
    }
}
