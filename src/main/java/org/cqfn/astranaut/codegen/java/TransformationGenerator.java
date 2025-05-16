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
import org.cqfn.astranaut.dsl.PatternMatchingMode;
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
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
     * Piece of code inserted to break.
     */
    private static final String BREAK = "break;";

    /**
     * Transformation rule.
     */
    private final TransformationDescriptor rule;

    /**
     * The condition of the rule is complex.
     */
    private final boolean complex;

    /**
     * Various flags that are set during source code generation by the transformation rule.
     */
    private final TransformationGeneratorFlags flags;

    /**
     * Constructor.
     * @param rule The transformation rule from which the source code is generated
     */
    public TransformationGenerator(final TransformationDescriptor rule) {
        this.rule = rule;
        this.complex = rule.hasOptionalOrRepeated() && rule.getLeft().size() > 1;
        this.flags = new TransformationGeneratorFlags();
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
        final Field instance = new Field("Converter", "INSTANCE", "The instance");
        instance.makePublic();
        instance.makeStatic();
        instance.makeFinal(String.format("new %s()", klass.getName()));
        klass.addField(instance);
        final Constructor ctor = klass.createConstructor();
        ctor.makePrivate();
        final Set<String> matchers = this.createConvertMethod(context, klass);
        this.createGetMinConsumedMethod(klass);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            klass
        );
        if (this.complex) {
            unit.addImport("java.util.Deque");
            unit.addImport("java.util.LinkedList");
        }
        unit.addImport("java.util.List");
        unit.addImport("java.util.Optional");
        if (this.flags.isCollectionsClassNeeded()) {
            unit.addImport("java.util.Collections");
        }
        if (this.flags.isListUtilsClassNeeded()) {
            unit.addImport("org.cqfn.astranaut.core.utils.ListUtils");
        }
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.ConversionResult");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Converter");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Extracted");
        unit.addImport("org.cqfn.astranaut.core.base.Factory");
        if (!(this.rule.getRight() instanceof UntypedHole)) {
            unit.addImport("org.cqfn.astranaut.core.base.Fragment");
        }
        unit.addImport("org.cqfn.astranaut.core.base.Node");
        if (this.rule.getRight() instanceof ResultingSubtreeDescriptor) {
            unit.addImport("org.cqfn.astranaut.core.base.DummyNode");
            unit.addImport("org.cqfn.astranaut.core.base.Builder");
        }
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
     * @return Matcher names that were used for the rule
     */
    private Set<String> createConvertMethod(final Context context, final Klass klass) {
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
                String.format("if (index + %d > list.size()) {", this.rule.getMinConsumed()),
                "break;",
                "}",
                "final Extracted extracted = new Extracted();"
            )
        );
        final ConditionGenerator cgen;
        if (this.rule.getLeft().size() == 1
            && this.rule.getLeft().get(0).getMatchingMode() == PatternMatchingMode.REPEATED) {
            cgen = new RepeatedNodeConditionGenerator(this.rule, context);
        } else if (this.complex) {
            cgen = new ComplexConditionGenerator(this.rule, context, klass);
        } else {
            cgen = new SimpleConditionGenerator(this.rule, context);
        }
        code.addAll(cgen.generate());
        final String consumed;
        if (this.rule.hasOptionalOrRepeated()) {
            consumed = "consumed";
        } else {
            consumed = String.valueOf(this.rule.getMinConsumed());
        }
        if (this.rule.getRight() instanceof UntypedHole) {
            code.addAll(
                Arrays.asList(
                    String.format(
                        "final Node node = extracted.getNodes(%d).get(0);",
                        ((UntypedHole) this.rule.getRight()).getNumber()
                    ),
                    String.format(
                        "result = Optional.of(new ConversionResult(node, %s));",
                        consumed
                    )
                )
            );
        } else {
            final ResultingSubtreeBuilderGenerator gen = new ResultingSubtreeBuilderGenerator(
                klass,
                new NameGenerator("root"),
                this.flags
            );
            final Method builder = gen.generate((ResultingSubtreeDescriptor) this.rule.getRight());
            if (gen.isExtractedParameterNeeded()) {
                code.add(
                    String.format(
                        "final Node node = %s.%s(factory, fragment, extracted);",
                        klass.getName(),
                        builder.getName()
                    )
                );
            } else {
                code.add(
                    String.format(
                        "final Node node = %s.%s(factory, fragment);",
                        klass.getName(),
                        builder.getName()
                    )
                );
            }
            code.addAll(
                Arrays.asList(
                    "if (node == DummyNode.INSTANCE) {",
                    TransformationGenerator.BREAK,
                    "}",
                    String.format(
                        "result = Optional.of(new ConversionResult(node, %s));",
                        consumed
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
        return cgen.getMatchers();
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
