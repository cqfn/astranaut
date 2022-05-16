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

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astgen.rules.Descriptor;
import org.cqfn.astgen.rules.DescriptorAttribute;

/**
 * Generates 'Matcher' classes.
 * A matcher checks if the node matches some structure, and extracts the data or (and) children.
 *
 * @since 1.0
 */
public final class MatcherGenerator {
    /**
     * The environment.
     */
    private final Environment env;

    /**
     * The package name.
     */
    private final String pkg;

    /**
     * The generator of class names.
     */
    private final ClassNameGenerator names;

    /**
     * The collection in which the generated units are placed.
     */
    private final Map<String, CompilationUnit> units;

    /**
     * Constructor.
     * @param env The environment.
     * @param pkg The package name.
     */
    public MatcherGenerator(final Environment env, final String pkg) {
        this.env = env;
        this.pkg = pkg;
        this.names = new ClassNameGenerator("Matcher");
        this.units = new TreeMap<>();
    }

    /**
     * Generates compilation unit from descriptor.
     * @param descriptor The descriptor
     * @return The name of generated class
     */
    public String generate(final Descriptor descriptor) {
        assert descriptor.getAttribute() == DescriptorAttribute.NONE;
        final String name = this.names.getName();
        final Klass klass = new Klass(
            "Checks if the node matches some structure, and extracts the data and children",
            name
        );
        final MatcherClassFiller filler = new MatcherClassFiller(this, klass, descriptor);
        filler.fill();
        final CompilationUnit unit = new CompilationUnit(
            this.env.getLicense(),
            this.pkg,
            klass
        );
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        if (filler.isCollectionsNeeded()) {
            unit.addImport("java.util.Collections");
        }
        if (filler.isArrayListNeeded()) {
            unit.addImport("java.util.ArrayList");
        }
        final String base = this.env.getBasePackage();
        unit.addImport(base.concat(".Matcher"));
        unit.addImport(base.concat(".Node"));
        this.units.put(String.format("rules%s%s", File.separator, name), unit);
        return name;
    }

    /**
     * Returns generated units.
     * @return The collection
     */
    public Map<String, CompilationUnit> getUnits() {
        return Collections.unmodifiableMap(this.units);
    }
}
