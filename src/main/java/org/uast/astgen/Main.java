/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.uast.astgen.codegen.java.Environment;
import org.uast.astgen.codegen.java.License;
import org.uast.astgen.codegen.java.ProgramGenerator;
import org.uast.astgen.exceptions.BaseException;
import org.uast.astgen.parser.ProgramParser;
import org.uast.astgen.rules.Program;
import org.uast.astgen.utils.FileConverter;
import org.uast.astgen.utils.FilesReader;
import org.uast.astgen.utils.LicenseValidator;
import org.uast.astgen.utils.PackageValidator;
import org.uast.astgen.utils.ProjectRootValidator;

/**
 * Main class.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.ImmutableField")
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
     * The name of file that contains license header.
     */
    @Parameter(
        names = { "--license", "-l" },
        validateWith = LicenseValidator.class,
        arity = 1,
        description = "The name of file that contain license header"
    )
    private String license;

    /**
     * The root of the target project.
     */
    @Parameter(
        names = { "--root", "-r" },
        validateWith = ProjectRootValidator.class,
        arity = 1,
        description = "The root of the project folder where generated files are stored"
    )
    private String root;

    /**
     * The package of the generated file.
     */
    @Parameter(
        names = { "--package", "-p" },
        validateWith = PackageValidator.class,
        arity = 1,
        description = "The path to the target folder of a generated source file"
    )
    private String pcg;

    /**
     * The help option.
     */
    @Parameter(names = "--help", help = true)
    private boolean help;

    /**
     * Private constructor with default values.
     */
    private Main() {
        this.help = false;
        this.license = "LICENSE_header.txt";
        this.root = "generated";
        this.pcg = "org.uast";
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
            final Environment env = new EnvironmentImpl();
            final ProgramGenerator generator = new ProgramGenerator(this.root, program, env);
            generator.generate();
        } catch (final BaseException exc) {
            LOG.severe(exc.getErrorMessage());
        }
    }

    /**
     * Environment implementation.
     *
     * @since 1.0
     */
    private class EnvironmentImpl implements Environment {
        /**
         * The license.
         */
        private final License license;

        /**
         * Constructor.
         */
        EnvironmentImpl() {
            this.license = new License(Main.this.license);
        }

        @Override
        public License getLicense() {
            return this.license;
        }

        @Override
        public String getRootPackage() {
            return Main.this.pcg;
        }

        @Override
        public String getBasePackage() {
            return "org.uast.uast.base";
        }

        @Override
        public List<String> getHierarchy(final String name) {
            return Collections.singletonList(name);
        }
    }
}
