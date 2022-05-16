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

import org.cqfn.astgen.rules.Node;
import org.cqfn.astgen.rules.Statement;

/**
 * Generates source code for rules that describe ordinary nodes.
 *
 * @since 1.0
 */
final class OrdinaryNodeGenerator extends BaseNodeGenerator {
    /**
     * The DSL statement.
     */
    private final Statement<Node> statement;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param statement The DSL statement
     */
    OrdinaryNodeGenerator(final Environment env, final Statement<Node> statement) {
        super(env);
        this.statement = statement;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        final Node rule = this.statement.getRule();
        final Klass klass = this.createClass(rule);
        new OrdinaryNodeClassConstructor(env, rule, klass).run();
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
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        unit.addImport("java.util.stream.Collectors");
        unit.addImport("java.util.stream.Stream");
        final Environment env = this.getEnv();
        final String base = env.getBasePackage();
        unit.addImport(base.concat(".Builder"));
        unit.addImport(base.concat(".ChildDescriptor"));
        if (!this.statement.getRule().isEmpty()) {
            unit.addImport(base.concat(".ChildrenMapper"));
            unit.addImport(base.concat(".ListUtils"));
        }
        unit.addImport(base.concat(".EmptyFragment"));
        unit.addImport(base.concat(".Fragment"));
        unit.addImport(base.concat(".Node"));
        unit.addImport(base.concat(".Type"));
        for (final String addition : env.getImports(this.statement.getRule().getType())) {
            unit.addImport(
                String.format(
                    "%s.green.%s",
                    env.getRootPackage(),
                    addition
                )
            );
        }
    }
}
