/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorAttribute;

/**
 * Generates converter classes.
 * A converter checks one rule described in DSL and convert the specified AST
 * built by a third-party parser to the unified format.
 *
 * @since 1.0
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
     * @param descriptor The descriptor
     * @param matcher The nme of the matcher class
     */
    public void generate(final Descriptor descriptor, final String matcher) {
        assert descriptor.getAttribute() == DescriptorAttribute.NONE;
        final String name = this.names.getName();
        final Klass klass = new Klass(
            "Converter describing DSL conversion rule",
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
        final String base = this.env.getBasePackage();
        unit.addImport(base.concat(".Builder"));
        unit.addImport(base.concat(".Converter"));
        unit.addImport(base.concat(".EmptyTree"));
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
