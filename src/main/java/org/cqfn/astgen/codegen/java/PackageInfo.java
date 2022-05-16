/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.cqfn.astgen.codegen.java;

import java.util.Objects;

/**
 * The package info generator.
 *
 * @since 1.0
 */
public final class PackageInfo implements JavaFile {
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

    @Override
    public void setVersion(final String str) {
        this.version = Objects.requireNonNull(str);
    }

    @Override
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
