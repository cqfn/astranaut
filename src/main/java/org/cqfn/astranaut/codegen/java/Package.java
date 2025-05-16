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
     * Parent package of the current package, or null if this is the root package.
     */
    private final Package parent;

    /**
     * Full name of the Java package.
     */
    private final String name;

    /**
     * Constructor for creating a package from a sequence of parts.
     *  This constructor parses the parts and creates the package object.
     *  The root package (if any) is implicitly created.
     * @param parts Parts of the package name (may contain dots, slashes, or backslashes).
     */
    public Package(final String... parts) {
        this(Package.parseParts(parts));
    }

    /**
     * Private constructor used for creating a package with the parent package.
     *  The package name is built by joining all parts into a single string.
     * @param parts List of parts of the package name
     */
    private Package(final List<String> parts) {
        this(
            Package.createPackage(parts.subList(0, parts.size() - 1)),
            String.join(".", parts)
        );
    }

    /**
     * Private constructor used to set the parent package and name of the current package.
     * @param parent The parent package of this package
     * @param name The name of this package
     */
    private Package(final Package parent, final String name) {
        this.parent = parent;
        this.name = name;
    }

    /**
     * Returns the subpackage that is inside the current package.
     *  The new subpackage is created by appending the parts to the current package name.
     * @param parts Parts of the subpackage name to append
     * @return Subpackage object representing the subpackage
     */
    public Package getSubpackage(final String... parts) {
        final List<String> parsed = Package.parseParts(parts);
        Package pkg = this;
        for (final String part : parsed) {
            pkg = new Package(pkg, String.format("%s.%s", pkg.name, part));
        }
        return pkg;
    }

    /**
     * Returns the parent package of the current package.
     * @return The parent package, or null if this is the root package
     */
    public Package getParent() {
        return this.parent;
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
     * Parses the parts of a package name (handling slashes, backslashes, and dots).
     *  The parts are normalized into a list of strings representing each component of the package.
     * @param parts Parts of the package name (can include slashes or backslashes)
     * @return A list of strings representing the parsed parts of the package
     */
    private static List<String> parseParts(final String... parts) {
        final List<String> list = new LinkedList<>();
        for (final String part : parts) {
            final String prepared = part
                .trim()
                .replace('\\', '.')
                .replace('/', '.');
            list.addAll(Arrays.asList(prepared.split("\\.")));
        }
        return list;
    }

    /**
     * Creates a new Package object based on the provided list of parts.
     *  If the list is empty, null is returned.
     * @param parts List of parts representing the package name
     * @return A Package object representing the given parts, or null if the list is empty
     */
    private static Package createPackage(final List<String> parts) {
        final Package pkg;
        if (parts.isEmpty()) {
            pkg = null;
        } else {
            pkg = new Package(parts);
        }
        return pkg;
    }
}
