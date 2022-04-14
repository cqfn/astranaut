/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Objects;

/**
 * The package info generator.
 *
 * @since 1.0
 */
public class PackageInfo {
    /**
     * The license.
     */
    private final License license;

    /**
     * The brief description.
     */
    private final String brief;

    /**
     * The version.
     */
    private String version;

    /**
     * The package name.
     */
    private final String pkg;

    /**
     * Constructor.
     * @param license The license
     * @param brief The brief description
     * @param pkg The package name
     */
    public PackageInfo(final License license, final String brief, final String pkg) {
        this.license = license;
        this.brief = brief;
        this.version = "1.0";
        this.pkg = pkg;
    }

    /**
     * Specifies the version.
     * @param str The version
     */
    public void setVersion(final String str) {
        this.version = Objects.requireNonNull(str);
    }

    /**
     * Generates source code.
     * @return Source code
     */
    public String generate() {
        final StringBuilder builder = new StringBuilder();
        if (this.license.isValid()) {
            builder.append(this.license.generate());
        }
        this.generateHeader(builder);
        builder.append("package ").append(this.pkg).append(";\n");
        return builder.toString();
    }

    /**
     * Generates package info header.
     * @param builder Where to generate
     */
    private void generateHeader(final StringBuilder builder) {
        builder.append("/**\n * ")
            .append(this.brief)
            .append(".\n *\n * @since ")
            .append(this.version)
            .append("\n */\n");
    }
}
