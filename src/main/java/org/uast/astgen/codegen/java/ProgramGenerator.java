/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.io.File;
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
public class ProgramGenerator {
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
        this.generateNodes();
    }

    /**
     * Generates source code for nodes.
     * @throws GeneratorException When can't generate
     */
    private void generateNodes() throws GeneratorException {
        final NodeGenerator nodegen = new NodeGenerator(this.env);
        final String version = this.env.getVersion();
        for (final Statement<Node> stmt : this.program.getNodes()) {
            final Node rule = stmt.getRule();
            final CompilationUnit unit;
            if (rule.isOrdinary()) {
                unit = nodegen.generate(stmt);
            } else {
                throw new IllegalStateException();
            }
            if (!version.isEmpty()) {
                unit.setVersion(version);
            }
            final String code = unit.generate();
            final String filename = this.getFilePath(stmt.getLanguage(), rule.getType());
            if (!this.env.isTestMode()) {
                final FilesWriter writer = new FilesWriter(filename);
                final boolean result = writer.writeStringNoExcept(code);
                if (!result) {
                    throw new CouldNotWriteFile(filename);
                }
            }
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
            subfolder = "green";
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
}
