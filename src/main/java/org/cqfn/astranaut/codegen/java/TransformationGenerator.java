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
import java.util.TreeSet;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.cqfn.astranaut.dsl.UntypedHole;

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
        final Set<String> matchers = new TreeSet<>();
        this.createConvertMethod(context, klass, matchers);
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
        final Package mpkg = context
            .getPackage()
            .getParent()
            .getParent()
            .getSubpackage("common", "matchers");
        for (final String matcher : matchers) {
            unit.addImport(String.format("%s.%s", mpkg, matcher));
        }
        return Collections.singleton(unit);
    }

    /**
     * Creates a "convert" method.
     * @param context Context
     * @param klass The class to which the method will be added
     * @param matchers Matcher names that were used for the rule
     */
    private void createConvertMethod(final Context context, final Klass klass,
        final Set<String> matchers) {
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
        if (!this.rule.hasOptionalOrRepeated()) {
            this.generateSimpleCondition(context, code, matchers);
        }
        code.addAll(
            Arrays.asList(
                "if (!matched) {",
                "break;",
                "}"
            )
        );
        if (this.rule.getRight() instanceof UntypedHole) {
            code.addAll(
                Arrays.asList(
                    String.format(
                        "final Node node = extracted.getNodes(%d).get(0);",
                        ((UntypedHole) this.rule.getRight()).getNumber()
                    ),
                    String.format(
                        "result = Optional.of(new ConversionResult(node, %d));",
                        this.consumed
                    )
                )
            );
        }
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
     * Generates code for a condition that checks whether all matchers
     *  from the left side of the transformation rule match corresponding elements.
     * @param context Context with available matchers
     * @param code Output list to append generated code
     * @param matchers Set to collect used matcher names
     */
    private void generateSimpleCondition(final Context context, final List<String> code,
        final Set<String> matchers) {
        final StringBuilder condition = new StringBuilder(128);
        condition.append("final boolean matched = ");
        final List<LeftSideItem> left = this.rule.getLeft();
        for (int index = 0; index < left.size(); index = index + 1) {
            if (index > 0) {
                condition.append(" && ");
            }
            final LeftSideItem item = left.get(index);
            final Klass matcher = context.getMatchers().get(item.toString(false));
            final String name = matcher.getName();
            matchers.add(name);
            condition
                .append(name)
                .append(".INSTANCE.match(list.get(");
            if (index > 0) {
                condition.append(index).append(" + index), extracted)");
            } else {
                condition.append("index), extracted)");
            }
        }
        condition.append(';');
        code.add(condition.toString());
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
