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

import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Describes 'package-info.java' file.
 * @since 1.0.0
 */
public final class PackageInfo implements JavaFileGenerator {
    /**
     * License.
     */
    private final License license;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * Package itself.
     */
    private final Package pkg;

    /**
     * Constructor.
     * @param license License
     * @param brief Brief description of the package
     * @param pkg Package itself
     */
    public PackageInfo(final License license, final String brief, final Package pkg) {
        this.license = license;
        this.doc = new JavaDoc(brief);
        this.pkg = pkg;
    }

    /**
     * Sets the version number. It will be added to JavaDoc.
     * @param value Version number
     */
    public void setVersion(final String value) {
        this.doc.setVersion(value);
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.license.build(indent, code);
        code.addEmpty();
        this.doc.build(indent, code);
        this.pkg.build(indent, code);
    }
}
