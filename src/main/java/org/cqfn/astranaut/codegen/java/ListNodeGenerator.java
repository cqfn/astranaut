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

import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates source code for rules that describe list nodes.
 *
 * @since 0.1.5
 */
final class ListNodeGenerator extends BaseNodeGenerator {
    /**
     * The DSL instruction.
     */
    private final Instruction<Node> instruction;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param instruction The DSL instruction
     */
    ListNodeGenerator(final Environment env, final Instruction<Node> instruction) {
        super(env);
        this.instruction = instruction;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        final Node rule = this.instruction.getRule();
        final Klass klass = this.createClass(rule);
        new ListNodeClassConstructor(env, rule, klass).run();
        final String pkg = this.getPackageName(this.instruction.getLanguage());
        final CompilationUnit unit = new CompilationUnit(env.getLicense(), pkg, klass);
        ListNodeGenerator.generateImports(unit);
        return unit;
    }

    /**
     * Generates imports block.
     * @param unit The compilation unit
     */
    private static void generateImports(final CompilationUnit unit) {
        unit.addImport("java.util.ArrayList");
        unit.addImport("java.util.Arrays");
        unit.addImport("java.util.Collections");
        unit.addImport("java.util.List");
        unit.addImport("java.util.Map");
        unit.addImport("org.cqfn.astranaut.core.utils.MapUtils");
        final String base = "org.cqfn.astranaut.core.base";
        unit.addImport(base.concat(".Builder"));
        unit.addImport(base.concat(".ChildDescriptor"));
        unit.addImport(base.concat(".EmptyFragment"));
        unit.addImport(base.concat(".Fragment"));
        unit.addImport(base.concat(".Node"));
        unit.addImport(base.concat(".Type"));
    }
}
