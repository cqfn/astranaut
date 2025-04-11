/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.dsl.TransformationDescriptor;

/**
 * Generates the compilation units described by the transformation rule
 *  (i.e., converter and matchers).
 * @since 1.0.0
 */
public final class TransformationGenerator extends RuleGenerator {
    /**
     * Transformation rule.
     */
    private final TransformationDescriptor rule;

    /**
     * Minimum number of nodes consumed by the rule.
     */
    private final int consumed;

    /**
     * Constructor.
     * @param rule The transformation rule from which the source code is generated
     */
    public TransformationGenerator(final TransformationDescriptor rule) {
        this.rule = rule;
        this.consumed = rule.getMinConsumed();
    }

    @Override
    public Rule getRule() {
        return this.rule;
    }

    @Override
    public Set<CompilationUnit> createUnits(final Context context) {
        final Klass klass = new Klass(
            context.getNextConverterName(),
            String.format(
                "Converter implementing the rule '%s'",
                this.rule.toString()
            )
        );
        klass.makePublic();
        klass.makeFinal();
        klass.setVersion(context.getVersion());
        klass.setImplementsList("Converter");
        this.createConvertMethod(klass);
        this.createGetMinConsumedMethod(klass);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            klass
        );
        unit.addImport("java.util.List");
        unit.addImport("java.util.Optional");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.ConversionResult");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Converter");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Extracted");
        unit.addImport("org.cqfn.astranaut.core.base.Factory");
        unit.addImport("org.cqfn.astranaut.core.base.Node");
        return Collections.singleton(unit);
    }

    /**
     * Creates a "convert" method.
     * @param klass The class to which the method will be added
     */
    private void createConvertMethod(final Klass klass) {
        final Method method = new Method(
            "Optional<ConversionResult>",
            "convert"
        );
        method.makePublic();
        method.addArgument(Strings.TYPE_NODE_LIST, "list");
        method.addArgument(Strings.TYPE_INT, "index");
        method.addArgument(Strings.TYPE_FACTORY, "factory");
        final List<String> code = new ArrayList<>(16);
        code.addAll(
            Arrays.asList(
                "Optional<ConversionResult> result = Optional.empty();",
                "do {",
                String.format("if (index + %d > list.size()) {", this.consumed),
                "break;",
                "}",
                "final Extracted extracted = new Extracted();"
            )
        );
        code.addAll(
            Arrays.asList(
                "} while (false);",
                "return result;"
            )
        );
        method.setBody(String.join("\n", code));
        klass.addMethod(method);
    }

    /**
     * Creates a "getMinConsumed" method.
     * @param klass The class to which the method will be added
     */
    private void createGetMinConsumedMethod(final Klass klass) {
        final Method method = new Method(
            Strings.TYPE_INT,
            "getMinConsumed"
        );
        method.makePublic();
        method.setBody(String.format("return %d;", this.rule.getMinConsumed()));
        klass.addMethod(method);
    }
}
