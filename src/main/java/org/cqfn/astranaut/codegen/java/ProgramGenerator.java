/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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
package org.cqfn.astranaut.codegen.java;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.exceptions.GeneratorCouldNotWriteFile;
import org.cqfn.astranaut.exceptions.GeneratorException;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Literal;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Program;

/**
 * Generates source code for the whole DSL program.
 *
 * @since 0.1.5
 */
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
     * The prepared environment collection required for generation,
     * one object for each programming language.
     */
    private final Map<String, Environment> envs;

    /**
     * The base environment.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param path The path where to generate
     * @param program The program
     * @param envs The environment collection required for generation
     */
    public ProgramGenerator(final String path, final Program program,
        final Map<String, Environment> envs) {
        this.path = path;
        this.program = program;
        this.envs = envs;
        this.env = envs.get("");
    }

    /**
     * Generates source code.
     * @throws GeneratorException When can't generate
     */
    public void generate() throws GeneratorException {
        this.generatePackages();
        this.generateNodes();
        this.generateLiterals();
        this.generateFactories();
        this.generateTransformations();
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
            final String name =
                language.substring(0, 1).toUpperCase(Locale.ENGLISH).concat(language.substring(1));
            final String pkg = language.toLowerCase(Locale.ENGLISH);
            this.generatePackage(
                String.format(
                    "This package contains nodes that describe the %s language",
                    name
                ),
                pkg
            );
            this.generatePackage(
                String.format(
                    "This package contains rules for processing %s language",
                    name
                ),
                String.format("%s.rules", pkg)
            );
        }
    }

    /**
     * Generates 'package-info.java' file for one language.
     * @param brief Brief description
     * @param pkg Package name
     * @throws GeneratorException When can't generate
     */
    private void generatePackage(final String brief, final String pkg)
        throws GeneratorException {
        final PackageInfo info = new PackageInfo(
            this.env.getLicense(),
            brief,
            String.format("%s.%s", this.env.getRootPackage(), pkg)
        );
        final String version = this.env.getVersion();
        if (!version.isEmpty()) {
            info.setVersion(version);
        }
        final String code = info.generate();
        final String filename = this.getFilePath(
            pkg.replace('.', File.separatorChar),
            "package-info"
        );
        this.createFile(filename, code);
    }

    /**
     * Generates source code for nodes.
     * @throws GeneratorException When can't generate
     */
    private void generateNodes() throws GeneratorException {
        final String version = this.env.getVersion();
        final NodeGenerator generator = new NodeGenerator(this.envs);
        for (final Instruction<Node> instruction : this.program.getNodes()) {
            final CompilationUnit unit = generator.generate(instruction);
            if (!version.isEmpty()) {
                unit.setVersion(version);
            }
            final String code = unit.generate();
            final String filename = this.getFilePath(
                instruction.getLanguage(), instruction.getRule().getType()
            );
            this.createFile(filename, code);
        }
    }

    /**
     * Generates source code for literals.
     * @throws GeneratorException When can't generate
     */
    private void generateLiterals() throws GeneratorException {
        final String version = this.env.getVersion();
        for (final Instruction<Literal> instruction : this.program.getLiterals()) {
            final String language = instruction.getLanguage();
            final Literal rule = instruction.getRule();
            final CompilationUnit unit =
                new LiteralGenerator(this.envs.get(language), instruction).generate();
            if (!version.isEmpty()) {
                unit.setVersion(version);
            }
            final String code = unit.generate();
            final String filename = this.getFilePath(language, rule.getType());
            this.createFile(filename, code);
        }
    }

    /**
     * Generates source code for factories.
     * @throws GeneratorException When can't generate
     */
    private void generateFactories() throws GeneratorException {
        this.generateFactory("");
        for (final String language : this.program.getNamesOfAllLanguages()) {
            this.generateFactory(language);
        }
    }

    /**
     * Generates source code for one factory.
     * @param language The programming language
     * @throws GeneratorException When can't generate
     */
    private void generateFactory(final String language) throws GeneratorException {
        final String version = this.env.getVersion();
        final FactoryGenerator generator = new FactoryGenerator(this.env, this.program, language);
        final CompilationUnit unit = generator.generate();
        if (!version.isEmpty()) {
            unit.setVersion(version);
        }
        final String code = unit.generate();
        final String filename = this.getFilePath(language, generator.getClassname());
        this.createFile(filename, code);
    }

    /**
     * Generates source code for transformations.
     * @throws GeneratorException When can't generate
     */
    private void generateTransformations() throws GeneratorException {
        final String version = this.env.getVersion();
        for (final String language : this.program.getNamesOfAllLanguages()) {
            final TransformationGenerator generator = new TransformationGenerator(
                this.envs.get(language),
                this.program.getTransformations(),
                language
            );
            generator.generate();
            final Map<String, CompilationUnit> units = generator.getUnits();
            for (final Map.Entry<String, CompilationUnit> entry : units.entrySet()) {
                final CompilationUnit unit = entry.getValue();
                if (!version.isEmpty()) {
                    unit.setVersion(version);
                }
                final String code = unit.generate();
                final String filename = this.getFilePath(language, entry.getKey());
                this.createFile(filename, code);
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
                throw new GeneratorCouldNotWriteFile(filename);
            }
        }
    }
}
