/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.List;
import java.util.Locale;
import org.uast.astgen.rules.Node;
import org.uast.astgen.rules.Statement;

/**
 * Generates source code for rules that describe abstract nodes.
 *
 * @since 1.0
 */
final class AbstractNodeGenerator extends BaseGenerator {
    /**
     * The DSL statement.
     */
    private final Statement<Node> statement;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param statement The DSL statement
     */
    AbstractNodeGenerator(final Environment env, final Statement<Node> statement) {
        super(env);
        this.statement = statement;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        final Node rule = this.statement.getRule();
        final String type = rule.getType();
        final Interface iface = new Interface(
            String.format("Node that describes the '%s' type", type),
            type
        );
        iface.setInterfaces("Node");
        this.defineGettersForTaggedFields(iface);
        final String pkg = this.getPackageName(this.statement.getLanguage());
        final CompilationUnit unit = new CompilationUnit(env.getLicense(), pkg, iface);
        this.generateImports(unit);
        return unit;
    }

    /**
     * Creates getter methods for tagged fields.
     * @param iface Where to create
     */
    private void defineGettersForTaggedFields(final Interface iface) {
        final List<TaggedChild> tags = this.getEnv().getTags(this.statement.getRule().getType());
        for (final TaggedChild child : tags) {
            if (!child.isOverridden()) {
                final String type = child.getType();
                final String tag = child.getTag();
                final MethodDescriptor getter = new MethodDescriptor(
                    String.format("Returns the child with the '%s' tag", tag),
                    String.format(
                        "get%s%s",
                        tag.substring(0, 1).toUpperCase(Locale.ENGLISH),
                        tag.substring(1)
                    )
                );
                getter.setReturnType(type, "The node");
                iface.addMethod(getter);
            }
        }
    }

    /**
     * Generates imports block.
     * @param unit The compilation unit
     */
    private void generateImports(final CompilationUnit unit) {
        final String base = this.getEnv().getBasePackage();
        unit.addImport(base.concat(".Node"));
    }
}
