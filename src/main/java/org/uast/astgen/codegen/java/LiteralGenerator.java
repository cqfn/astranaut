/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import org.uast.astgen.rules.Literal;
import org.uast.astgen.rules.Statement;

/**
 * Generates source code for rules that describe literals.
 *
 * @since 1.0
 */
final class LiteralGenerator extends BaseGenerator {
    /**
     * The DSL statement.
     */
    private final Statement<Literal> statement;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param statement The DSL statement
     */
    LiteralGenerator(final Environment env, final Statement<Literal> statement) {
        super(env);
        this.statement = statement;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        final Literal rule = this.statement.getRule();
        final Klass klass = this.createClass(rule);
        new LiteralClassConstructor(env, rule, klass).run();
        final String pkg = this.getPackageName(this.statement.getLanguage());
        final CompilationUnit unit = new CompilationUnit(env.getLicense(), pkg, klass);
        this.generateImports(unit);
        return unit;
    }

    /**
     * Creates class for node construction.
     * @param rule The rule
     * @return The class constructor
     */
    private Klass createClass(final Literal rule) {
        final String type = rule.getType();
        final Klass klass = new Klass(
            String.format("Node that describes the '%s' type", type),
            type
        );
        klass.makeFinal();
        final List<String> hierarchy = this.getEnv().getHierarchy(type);
        if (hierarchy.size() > 1) {
            klass.setInterfaces(hierarchy.get(1));
        } else {
            klass.setInterfaces("Node");
        }
        return klass;
    }

    /**
     * Generates imports block.
     * @param unit The compilation unit
     */
    private void generateImports(final CompilationUnit unit) {
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        unit.addImport("java.util.stream.Collectors");
        unit.addImport("java.util.stream.Stream");
        final String base = this.getEnv().getBasePackage();
        unit.addImport(base.concat(".Builder"));
        unit.addImport(base.concat(".ChildDescriptor"));
        unit.addImport(base.concat(".EmptyFragment"));
        unit.addImport(base.concat(".Fragment"));
        unit.addImport(base.concat(".Node"));
        unit.addImport(base.concat(".Type"));
    }
}
