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
package org.cqfn.astranaut.codegen.java;

import java.util.Set;
import java.util.TreeSet;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Compilation unit, that is, whatever is needed to generate a Java source file.
 * @since 1.0.0
 */
public final class CompilationUnit implements JavaFileGenerator {
    /**
     * License.
     */
    private final License license;

    /**
     * Package.
     */
    private final Package pkg;

    /**
     * Set of imports.
     */
    private final Set<String> imports;

    /**
     * Class or interface.
     */
    private final ClassOrInterface coi;

    /**
     * Constructor.
     * @param license License.
     * @param pkg Package.
     * @param coi Class or interface.
     */
    public CompilationUnit(final License license, final Package pkg, final ClassOrInterface coi) {
        this.license = license;
        this.pkg = pkg;
        this.imports = new TreeSet<>();
        this.coi = coi;
    }

    /**
     * Adds the name of the imported class to the list.
     * @param klass Full name of the imported class
     */
    public void addImport(final String klass) {
        final String value = klass.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.imports.add(value);
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.license.build(indent, code);
        this.pkg.build(indent, code);
        code.addEmpty();
        if (!this.imports.isEmpty()) {
            for (final String klass : this.imports) {
                code.add(indent, String.format("import %s;", klass));
            }
            code.addEmpty();
        }
        this.coi.build(indent, code);
    }

    /**
     * Returns the name of the compilation unit file.
     * @return Java filename
     */
    public String getFileName() {
        return this.coi.getName().concat(".java");
    }
}
