/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Objects;

/**
 * Java compilation unit.
 *
 * @since 1.0
 */
public class CompilationUnit {
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

    /**
     * Generates source code.
     * @return Source code
     */
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
