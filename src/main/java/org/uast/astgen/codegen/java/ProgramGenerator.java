/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.io.File;
import java.util.Locale;
import org.uast.astgen.exceptions.CouldNotWriteFile;
import org.uast.astgen.exceptions.GeneratorException;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Program;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.utils.FilesWriter;

/**
 * Generates source code for the whole DSL program.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.CloseResource")
public final class ProgramGenerator {
    /**
     * The 'green' package name.
     */
    private static final String GREEN = "green";

    /**
     * The path where to generate.
     */
    private final String path;

    /**
     * The program.
      */
    private final Program program;

    /**
     * The environment required for generation.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param path The path where to generate
     * @param program The program
     * @param env The environment required for generation
     */
    public ProgramGenerator(final String path, final Program program, final Environment env) {
        this.path = path;
        this.program = program;
        this.env = env;
    }

    /**
     * Generates source code.
     * @throws GeneratorException When can't generate
     */
    public void generate() throws GeneratorException {
        this.generatePackages();
        this.generateNodes();
    }

    /**
     * Generates 'package-info.java' files.
     * @throws GeneratorException When can't generate
     */
    private void generatePackages() throws GeneratorException {
        this.generatePackage(
            "This package contains unified nodes",
            ProgramGenerator.GREEN
        );
        for (final String language : this.program.getNamesOfAllLanguages()) {
            this.generatePackage(
                String.format(
                    "This package contains nodes that describe the %s%s programming language",
                    language.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    language.substring(1)
                ),
                language.toLowerCase(Locale.ENGLISH)
            );
        }
    }

    /**
     * Generates 'package-info.java' file for one language.
     * @param brief Brief description
     * @param language Language name
     * @throws GeneratorException When can't generate
     */
    private void generatePackage(final String brief, final String language)
        throws GeneratorException {
        final PackageInfo info = new PackageInfo(
            this.env.getLicense(),
            brief,
            String.format("%s.%s", this.env.getRootPackage(), language)
        );
        final String version = this.env.getVersion();
        if (!version.isEmpty()) {
            info.setVersion(version);
        }
        final String code = info.generate();
        final String filename = this.getFilePath(language, "package-info");
        this.createFile(filename, code);
    }

    /**
     * Generates source code for nodes.
     * @throws GeneratorException When can't generate
     */
    private void generateNodes() throws GeneratorException {
        final String version = this.env.getVersion();
        for (final Statement<Node> stmt : this.program.getNodes()) {
            final Node rule = stmt.getRule();
            final CompilationUnit unit;
            if (rule.isOrdinary()) {
                unit = new OrdinaryNodeGenerator(this.env, stmt).generate();
            } else if (rule.isAbstract()) {
                unit = new AbstractNodeGenerator(this.env, stmt).generate();
            } else {
                throw new IllegalStateException();
            }
            if (!version.isEmpty()) {
                unit.setVersion(version);
            }
            final String code = unit.generate();
            final String filename = this.getFilePath(stmt.getLanguage(), rule.getType());
            this.createFile(filename, code);
        }
    }

    /**
     * Generates full file path for saving compilation unit.
     * @param language The programming language for which the rule is applied
     * @param name The rule name
     * @return The file path
     */
    private String getFilePath(final String language, final String name) {
        final String subfolder;
        if (language.isEmpty()) {
            subfolder = ProgramGenerator.GREEN;
        } else {
            subfolder = language;
        }
        return String.format(
            "%s%c%s%c%s%c%s.java",
            this.path,
            File.separatorChar,
            this.env.getRootPackage().replace('.', File.separatorChar),
            File.separatorChar,
            subfolder,
            File.separatorChar,
            name
        );
    }

    /**
     * Writes a file to a file system.
     * @param filename The file name
     * @param code The file content
     * @throws GeneratorException In case if could not create
     */
    private void createFile(final String filename, final String code) throws GeneratorException {
        if (!this.env.isTestMode()) {
            final FilesWriter writer = new FilesWriter(filename);
            final boolean result = writer.writeStringNoExcept(code);
            if (!result) {
                throw new CouldNotWriteFile(filename);
            }
        }
    }
}
