/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Locale;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Statement;

/**
 * Generates source code for rules that describe nodes.
 *
 * @since 1.0
 */
public final class NodeGenerator {
    /**
     * The environment.
     */
    private final Environment env;

    /**
     * Constructor.
     * @param env The environment required for generation.
     */
    public NodeGenerator(final Environment env) {
        this.env = env;
    }

    /**
     * Generates Java source code.
     * @param statement DSL statement
     * @return Source code
     */
    public String generate(final Statement<Node> statement) {
        final String language = statement.getLanguage();
        final String root = this.env.getRootPackage();
        final String pkg;
        if (language.isEmpty()) {
            pkg = root.concat(".green");
        } else {
            pkg = root.concat(language.toLowerCase(Locale.ENGLISH));
        }
        final Node rule = statement.getRule();
        final String type = rule.getType();
        final Klass klass = new Klass(
            String.format("Node that describes the '%s' type", type),
            type
        );
        klass.makeFinal();
        klass.setInterfaces("Node");
        final CompilationUnit unit = new CompilationUnit(this.env.getLicense(), pkg, klass);
        this.generateImports(unit);
        return unit.generate();
    }

    /**
     * Generates imports block.
     * @param unit The compilation unit
     */
    private void generateImports(final CompilationUnit unit) {
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        final String base = this.env.getBasePackage();
        unit.addImport(base.concat(".Builder"));
        unit.addImport(base.concat(".ChildDescriptor"));
        unit.addImport(base.concat(".ChildrenMapper"));
        unit.addImport(base.concat(".EmptyFragment"));
        unit.addImport(base.concat(".Fragment"));
        unit.addImport(base.concat(".Node"));
        unit.addImport(base.concat(".Type"));
    }
}
