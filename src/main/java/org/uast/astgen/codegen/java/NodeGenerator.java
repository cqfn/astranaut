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
public class NodeGenerator {
    /**
     * The license.
     */
    private final License license;

    /**
     * The root package name.
     */
    private final String root;

    /**
     * Constructor.
     * @param license The license
     * @param pkg The root package name
     */
    public NodeGenerator(final License license, final String pkg) {
        this.license = license;
        this.root = pkg;
    }

    /**
     * Generates Java source code.
     * @param statement DSL statement
     * @return Source code
     */
    public String generate(final Statement<Node> statement) {
        final String language = statement.getLanguage();
        final String pkg;
        if (language.isEmpty()) {
            pkg = this.root.concat(".green");
        } else {
            pkg = this.root.concat(language.toLowerCase(Locale.ENGLISH));
        }
        final Node rule = statement.getRule();
        final String type = rule.getType();
        final Klass klass = new Klass(
            String.format("Node that describes the '%s' type", type),
            type
        );
        klass.makeFinal();
        klass.setInterfaces("Node");
        final CompilationUnit unit = new CompilationUnit(this.license, pkg, klass);
        return unit.generate();
    }
}
