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
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.codegen.java.CompilationUnit;
import org.cqfn.astranaut.codegen.java.Context;
import org.cqfn.astranaut.codegen.java.License;
import org.cqfn.astranaut.codegen.java.Package;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Generates source code from the described rules.
 * @since 1.0.0
 */
public final class Generate implements Action {
    @Override
    public void perform(final List<Rule> rules, final List<String> args) throws BaseException {
        final ArgumentParser options = new ArgumentParser();
        options.parse(args);
        final License license = new License("Copyright 2024 John Doe");
        final Package pkg = new Package("tree");
        final Context.Constructor cctor = new Context.Constructor();
        cctor.setLicense(license);
        cctor.setPackage(pkg);
        cctor.setVersion("1.0.0");
        final Context context = cctor.createContext();
        for (final Rule rule : rules) {
            final String subfolder = "nodes";
            String language = rule.getLanguage();
            if (language.isEmpty()) {
                language = "common";
            }
            final File folder = Paths.get(options.getOutput(), language, subfolder).toFile();
            folder.mkdirs();
            final RuleGenerator generator = rule.createGenerator();
            if (generator != null) {
                final Set<CompilationUnit> units = generator.createUnits(context);
                for (final CompilationUnit unit : units) {
                    final String code = unit.generateJavaCode();
                    new FilesWriter(
                        new File(
                            folder,
                            ((NodeDescriptor) rule).getName().concat(".java")
                        )
                        .getAbsolutePath()
                    )
                        .writeStringNoExcept(code);
                }
            }
        }
    }
}
