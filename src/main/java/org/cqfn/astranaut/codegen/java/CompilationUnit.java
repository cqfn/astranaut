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

import java.util.Objects;

/**
 * Java compilation unit.
 *
 * @since 0.1.5
 */
public final class CompilationUnit implements JavaFile {
    /**
     * The license.
     */
    private final License license;

    /**
     * The package name.
     */
    private final String pkg;

    /**
     * The imports block.
     */
    private final Imports imports;

    /**
     * The type, i.e. interface or class description.
     */
    private final Type type;

    /**
     * Constructor.
     * @param license The license
     * @param pkg The package name
     * @param type The type, i.e. interface or class description
     */
    public CompilationUnit(final License license, final String pkg, final Type type) {
        this.license = Objects.requireNonNull(license);
        this.pkg = Objects.requireNonNull(pkg);
        this.imports = new Imports();
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Adds import dependency.
     * @param item The dependency
     */
    public void addImport(final String item) {
        this.imports.addItem(item);
    }

    @Override
    public void setVersion(final String version) {
        this.type.setVersion(version);
    }

    @Override
    public String generate() {
        final StringBuilder builder = new StringBuilder(256);
        if (this.license.isValid()) {
            builder.append(this.license.generate());
        }
        if (!this.pkg.isEmpty()) {
            builder.append("package ").append(this.pkg).append(";\n\n");
        }
        if (this.imports.hasItems()) {
            builder.append(this.imports.generate());
        }
        builder.append(this.type.generate(0));
        return builder.toString();
    }
}
