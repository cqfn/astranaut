/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Statement;

/**
 * Generates source code for rules that describe list nodes.
 *
 * @since 1.0
 */
final class ListNodeGenerator extends BaseNodeGenerator {
    /**
     * The DSL statement.
     */
    private final Statement<Node> statement;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param statement The DSL statement
     */
    ListNodeGenerator(final Environment env, final Statement<Node> statement) {
        super(env);
        this.statement = statement;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        final Node rule = this.statement.getRule();
        final Klass klass = this.createClass(rule);
        final String pkg = this.getPackageName(this.statement.getLanguage());
        final CompilationUnit unit = new CompilationUnit(env.getLicense(), pkg, klass);
        this.generateImports(unit);
        return unit;
    }

    /**
     * Generates imports block.
     * @param unit The compilation unit
     */
    private void generateImports(final CompilationUnit unit) {
        unit.addImport("java.util.ArrayList");
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        final String base = this.getEnv().getBasePackage();
        unit.addImport(base.concat(".Builder"));
        unit.addImport(base.concat(".ChildDescriptor"));
        unit.addImport(base.concat(".EmptyFragment"));
        unit.addImport(base.concat(".Fragment"));
        unit.addImport(base.concat(".Node"));
        unit.addImport(base.concat(".Type"));
    }
}
