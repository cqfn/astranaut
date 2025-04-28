/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.codegen.java.CompilationUnit;
import org.cqfn.astranaut.codegen.java.Context;
import org.cqfn.astranaut.codegen.java.FactoryGenerator;
import org.cqfn.astranaut.codegen.java.Klass;
import org.cqfn.astranaut.codegen.java.LeftSideGenerationContext;
import org.cqfn.astranaut.codegen.java.License;
import org.cqfn.astranaut.codegen.java.Package;
import org.cqfn.astranaut.codegen.java.PackageInfo;
import org.cqfn.astranaut.codegen.java.ProviderGenerator;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.codegen.java.TransformerGenerator;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
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

    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public void perform(final Program program, final List<String> args) throws BaseException {
        this.options.parse(args);
        this.license = new License(this.options.getLicence());
        this.basepkg = new Package(this.options.getPackage());
        this.root = Paths.get(
            this.options.getOutput(),
            this.options.getPackage().replace('.', '/')
        );
        try {
            if (Files.exists(this.root)) {
                Files.walk(this.root)
                    .sorted(Comparator.reverseOrder())
                    .filter(p -> Files.isWritable(p))
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
            Files.createDirectories(this.root);
        } catch (final IOException exception) {
            throw new CommonCliException(
                String.format(
                    "Cannot create destination folder '%s'",
                    this.root.toAbsolutePath()
                )
            );
        }
        final Map<String, Klass> matchers = this.generateMatchersIfAny(program);
        this.factories = new FactoryGenerator(program);
        for (final String language : program.getAllLanguages()) {
            this.generateNodes(program, language);
            this.generateTransformationsIfAny(program, language, matchers);
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
        final CompilationUnit provider = new ProviderGenerator(program).createUnit(context);
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
        for (final NodeDescriptor rule : program.getNodeDescriptorsByLanguage(language).values()) {
            final RuleGenerator generator = rule.createGenerator();
            final Set<CompilationUnit> units = generator.createUnits(context);
            for (final CompilationUnit unit : units) {
                Generate.writeFile(new File(folder, unit.getFileName()), unit.generateJavaCode());
            }
        }
    }

    /**
     * Generates matchers if there are transformation descriptors in the given program.
     *  This method retrieves all transformation descriptors from the program and,
     *  if any exist, invokes {@link #generateMatchers(List)} to generate the matchers.
     *  If no transformation descriptors are found, an empty map is returned.
     * @param program The program containing transformation descriptors
     * @return A map of matcher classes mapped to their textual representations.
     *  If no matchers are generated, returns an empty map
     * @throws BaseException If an error occurs during matcher generation
     */
    private Map<String, Klass> generateMatchersIfAny(final Program program) throws BaseException {
        final List<TransformationDescriptor> rules = program.getAllTransformationDescriptors();
        final Map<String, Klass> matchers;
        if (rules.isEmpty()) {
            matchers = Collections.emptyMap();
        } else {
            matchers = Collections.unmodifiableMap(this.generateMatchers(rules));
        }
        return matchers;
    }

    /**
     * Generates matcher classes based on the given transformation descriptors.
     * @param rules The list of transformation descriptors for which matchers are generated
     * @return A map of matcher classes mapped to their textual representations
     * @throws BaseException If an error occurs during file writing or matcher generation
     */
    private Map<String, Klass> generateMatchers(final List<TransformationDescriptor> rules)
        throws BaseException {
        final Package pkg = this.basepkg.getSubpackage("common.matchers");
        final File folder = this.root.resolve("common/matchers").toFile();
        folder.mkdirs();
        final PackageInfo info = new PackageInfo(
            this.license,
            "This package contains matchers that map subtrees to some pattern and extract nodes and data when matched",
            pkg
        );
        info.setVersion(this.options.getVersion());
        Generate.writeFile(new File(folder, "package-info.java"), info.generateJavaCode());
        final LeftSideGenerationContext context = new LeftSideGenerationContext();
        for (final TransformationDescriptor rule : rules) {
            for (final LeftSideItem item : rule.getLeft()) {
                item.generateMatcher(context);
            }
        }
        final Map<String, Klass> matchers = context.getMatchers();
        for (final Klass klass : matchers.values()) {
            klass.setVersion(this.options.getVersion());
            klass.makePublic();
            klass.makeFinal();
            klass.setImplementsList("Matcher");
            final CompilationUnit unit = new CompilationUnit(this.license, pkg, klass);
            unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Extracted");
            unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Matcher");
            unit.addImport("org.cqfn.astranaut.core.base.Node");
            Generate.writeFile(new File(folder, unit.getFileName()), unit.generateJavaCode());
        }
        return matchers;
    }

    /**
     * Generates transformations for the specified programming language, if any.
     * @param program Program implemented in DSL
     * @param language Language name
     * @param matchers Map of matcher classes mapped to their textual representations
     * @throws BaseException If files cannot be generated
     */
    private void generateTransformationsIfAny(final Program program, final String language,
        final Map<String, Klass> matchers) throws BaseException {
        final List<TransformationDescriptor> rules =
            program.getTransformationDescriptorsByLanguage(language);
        if (!rules.isEmpty()) {
            this.generateTransformations(rules, language, matchers);
        }
    }

    /**
     * Generates transformations for the specified programming language.
     * @param rules List of transformation rules
     * @param language Language name
     * @param matchers Map of matcher classes mapped to their textual representations
     * @throws BaseException If files cannot be generated
     */
    private void generateTransformations(
        final List<TransformationDescriptor> rules, final String language,
        final Map<String, Klass> matchers) throws BaseException {
        final Package pkg = this.basepkg.getSubpackage(language, "rules");
        final File folder = this.root.resolve(String.format("%s/rules", language)).toFile();
        folder.mkdirs();
        final String brief;
        if (language.equals("common")) {
            brief = "This package contains transformation rules for common ('green') nodes";
        } else {
            brief = String.format(
                "This package contains transformation rules for %s%s language",
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
        cct.setMatchers(matchers);
        final Context context = cct.createContext();
        final CompilationUnit transformer =
            TransformerGenerator.INSTANCE.createUnit(language, context);
        Generate.writeFile(
            new File(folder, transformer.getFileName()),
            transformer.generateJavaCode()
        );
        for (final TransformationDescriptor rule : rules) {
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
