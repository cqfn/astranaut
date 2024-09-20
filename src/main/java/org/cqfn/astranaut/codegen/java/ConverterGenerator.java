/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.rules.Transformation;

/**
 * Generates converter classes.
 * A converter checks one rule described in DSL and convert the specified AST
 * built by a third-party parser to the unified format.
 *
 * @since 0.1.5
 */
public final class ConverterGenerator {
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
    public ConverterGenerator(final Environment env, final String pkg) {
        this.env = env;
        this.pkg = pkg;
        this.names = new ClassNameGenerator("Rule");
        this.units = new TreeMap<>();
    }

    /**
     * Generates compilation unit from descriptor.
     * @param rule The transformation rule
     * @param matcher The name of the matcher class
     */
    public void generate(final Transformation rule, final String matcher) {
        final Descriptor descriptor = rule.getRight();
        final DescriptorAttribute attrib = descriptor.getAttribute();
        assert attrib == DescriptorAttribute.NONE || attrib == DescriptorAttribute.HOLE;
        final String name = this.names.getName();
        final StringBuilder brief = new StringBuilder(128);
        brief.append("Checks that the node matches the conversion rule:\n");
        final String description = rule.toString();
        if (description.length() < Entity.MAX_LINE_LENGTH) {
            brief.append("  ").append(description).append('\n');
        } else {
            brief.append(rule.toStringIndented(2));
        }
        brief.append("and performs the conversion if possible");
        final Klass klass = new Klass(
            brief.toString(),
            name
        );
        final ConverterClassFiller filler = new ConverterClassFiller(klass, descriptor, matcher);
        filler.fill();
        final CompilationUnit unit = new CompilationUnit(
            this.env.getLicense(),
            this.pkg,
            klass
        );
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        unit.addImport("java.util.TreeMap");
        if (filler.isLinkedListNeeded()) {
            unit.addImport("java.util.LinkedList");
        }
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Converter");
        final String base = "org.cqfn.astranaut.core.base";
        if (attrib == DescriptorAttribute.NONE) {
            unit.addImport(base.concat(".Builder"));
        }
        unit.addImport(base.concat(".DummyNode"));
        unit.addImport(base.concat(".Factory"));
        unit.addImport(base.concat(".Node"));
        this.units.put(String.format("rules%s%s", File.separator, name), unit);
    }

    /**
     * Returns generated units.
     * @return The collection
     */
    public Map<String, CompilationUnit> getUnits() {
        return Collections.unmodifiableMap(this.units);
    }
}
