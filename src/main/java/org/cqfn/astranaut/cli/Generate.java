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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.cqfn.astranaut.codegen.java.CompilationUnit;
import org.cqfn.astranaut.codegen.java.Context;
import org.cqfn.astranaut.codegen.java.FactoryGenerator;
import org.cqfn.astranaut.codegen.java.FactoryProviderGenerator;
import org.cqfn.astranaut.codegen.java.License;
import org.cqfn.astranaut.codegen.java.Package;
import org.cqfn.astranaut.codegen.java.PackageInfo;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Generates source code from the described rules.
 * @since 1.0.0
 */
public final class Generate implements Action {
    /**
     * Command line options.
     */
    private final ArgumentParser options;

    /**
     * License object (required for all generated files).
     */
    private License license;

    /**
     * Base package object.
     */
    private Package basepkg;

    /**
     * Path to the folder where files and folders are generated.
     */
    private Path root;

    /**
     * Generates factories for nodes.
     */
    private FactoryGenerator factories;

    /**
     * Constructor.
     */
    public Generate() {
        this.options = new ArgumentParser();
    }

    @Override
    public void perform(final Program program, final List<String> args) throws BaseException {
        this.options.parse(args);
        this.license = new License(this.options.getLicence());
        this.basepkg = new Package(this.options.getPackage());
        this.root = Paths.get(
            this.options.getOutput(),
            this.options.getPackage().replace('.', '/')
        );
        this.factories = new FactoryGenerator(program);
        for (final String language : program.getAllLanguages()) {
            this.generateNodes(program, language);
        }
        final PackageInfo info = new PackageInfo(
            this.license,
            "Nodes describing syntax trees, and algorithms to process them, generated from the description in the DSL language",
            this.basepkg
        );
        info.setVersion(this.options.getVersion());
        Generate.writeFile(
            new File(this.root.toString(), "package-info.java"),
            info.generateJavaCode()
        );
        final Context.Constructor cct = new Context.Constructor();
        cct.setLicense(this.license);
        cct.setPackage(this.basepkg);
        cct.setVersion(this.options.getVersion());
        final Context context = cct.createContext();
        final CompilationUnit provider = new FactoryProviderGenerator(program).createUnit(context);
        Generate.writeFile(
            new File(this.root.toString(), provider.getFileName()),
            provider.generateJavaCode()
        );
    }

    /**
     * Generates nodes describing the syntax of the specified language.
     * @param program Program implemented in DSL
     * @param language Language name
     * @throws BaseException If files cannot be generated
     */
    private void generateNodes(final Program program, final String language) throws BaseException {
        final Package pkg = this.basepkg.getSubpackage(language, "nodes");
        final File folder = this.root.resolve(String.format("%s/nodes", language)).toFile();
        folder.mkdirs();
        final String brief;
        if (language.equals("common")) {
            brief = "This package contains common ('green') nodes";
        } else {
            brief = String.format(
                "This package contains nodes that describe the syntax of %s%s language",
                language.substring(0, 1).toUpperCase(Locale.ENGLISH),
                language.substring(1)
            );
        }
        final PackageInfo info = new PackageInfo(this.license, brief, pkg);
        info.setVersion(this.options.getVersion());
        Generate.writeFile(new File(folder, "package-info.java"), info.generateJavaCode());
        final Context.Constructor cct = new Context.Constructor();
        cct.setLicense(this.license);
        cct.setPackage(pkg);
        cct.setVersion(this.options.getVersion());
        final Context context = cct.createContext();
        final CompilationUnit factory = this.factories.createUnit(language, context);
        Generate.writeFile(new File(folder, factory.getFileName()), factory.generateJavaCode());
        for (final NodeDescriptor rule : program.getNodeDescriptorsForLanguage(language)) {
            final RuleGenerator generator = rule.createGenerator();
            final Set<CompilationUnit> units = generator.createUnits(context);
            for (final CompilationUnit unit : units) {
                Generate.writeFile(new File(folder, unit.getFileName()), unit.generateJavaCode());
            }
        }
    }

    /**
     * Writes a file.
     * @param file File
     * @param content File content
     * @throws CliException In case it is not possible to write a file.
     */
    private static void writeFile(final File file, final String content) throws CliException {
        final boolean result = new FilesWriter(file.getAbsolutePath()).writeStringNoExcept(content);
        if (!result) {
            throw new CannotWriteFile(file.getName());
        }
    }

    /**
     * Exception 'Cannot write file'.
     * @since 1.0.0
     */
    private static final class CannotWriteFile extends CliException {
        /**
         * Version identifier.
         */
        private static final long serialVersionUID = -1;

        /**
         * The name of the file that could not be written.
         */
        private final String name;

        /**
         * Constructor.
         * @param name The name of the file that could not be written
         */
        private CannotWriteFile(final String name) {
            this.name = name;
        }

        @Override
        public String getErrorMessage() {
            return String.format("Cannot write file: '%s'", this.name);
        }
    }
}
