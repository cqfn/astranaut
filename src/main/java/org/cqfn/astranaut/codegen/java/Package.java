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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Entity representing the name of a Java package.
 * @since 1.0.0
 */
public final class Package implements Entity {
    /**
     * Full name of the Java package.
     */
    private final String name;

    /**
     * Constructor.
     * @param parts Parts of the package name
     */
    public Package(final String... parts) {
        this.name = Package.prepareName(parts);
    }

    /**
     * Returns the packet that is inside the current packet.
     * @param parts Parts of the subpackage name
     * @return Subpackage
     */
    public Package getSubpackage(final String... parts) {
        final String[] args = new String[parts.length + 1];
        args[0] = this.name;
        System.arraycopy(parts, 0, args, 1, parts.length);
        return new Package(args);
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        code.add(indent, String.format("package %s;", this.name));
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Prepares the name of the package from the parts.
     * @param parts Parts of the package name
     * @return Full name of the Java package
     */
    private static String prepareName(final String... parts) {
        final List<String> list = new LinkedList<>();
        for (final String part : parts) {
            final String prepared = part
                .trim()
                .replace('\\', '.')
                .replace('/', '.');
            list.addAll(Arrays.asList(prepared.split("\\.")));
        }
        return String.join(".", list);
    }
}
