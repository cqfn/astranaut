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
import java.util.Locale;
import java.util.Set;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.dsl.PatternMatchingMode;
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
import org.cqfn.astranaut.dsl.RightSideItem;
import org.cqfn.astranaut.dsl.Rule;
import org.cqfn.astranaut.dsl.StaticString;
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
     * Flag indicating that 'Collection' class is needed.
     */
    private boolean collections;

    /**
     * Constructor.
     * @param rule The transformation rule from which the source code is generated
     */
    public TransformationGenerator(final TransformationDescriptor rule) {
        this.rule = rule;
        this.complex = rule.hasOptionalOrRepeated() && rule.getLeft().size() > 1;
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
        if (this.collections) {
            unit.addImport("java.util.Collections");
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
        } else if (this.rule.getRight() instanceof ResultingSubtreeDescriptor) {
            final Pair<Method, Boolean> builder = this.generateBuilder(
                klass,
                new NameGenerator("root"),
                (ResultingSubtreeDescriptor) this.rule.getRight()
            );
            if (builder.getValue()) {
                code.add(
                    String.format(
                        "final Node node = %s.%s(factory, fragment, extracted);",
                        klass.getName(),
                        builder.getKey().getName()
                    )
                );
            } else {
                code.add(
                    String.format(
                        "final Node node = %s.%s(factory, fragment);",
                        klass.getName(),
                        builder.getKey().getName()
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
     * Creates a function that creates a node based on its descriptor,
     *  using the extracted nodes and data.
     * @param klass The class in which the method is created.
     * @param names Method name generator
     * @param descriptor Node descriptor
     * @return Created method with an indication whether to pass extracted data to it or not
     */
    private Pair<Method, Boolean> generateBuilder(final Klass klass,
        final NameGenerator names, final ResultingSubtreeDescriptor descriptor) {
        final String name = names.nextName();
        final Method method = new Method(
            Strings.TYPE_NODE,
            String.format(
                "build%s%s",
                name.substring(0, 1).toUpperCase(Locale.ENGLISH),
                name.substring(1)
            ),
            String.format(
                "Constructs a node based on the descriptor '%s'",
                descriptor.toString()
            )
        );
        klass.addMethod(method);
        method.makePrivate();
        method.makeStatic();
        method.addArgument(
            Strings.TYPE_FACTORY,
            "factory",
            "Factory for creating nodes"
        );
        method.setReturnsDescription("Created node");
        final List<String> code = new ArrayList<>(16);
        code.addAll(
            Arrays.asList(
                "Node result = DummyNode.INSTANCE;",
                String.format(
                    "final Builder builder = factory.createBuilder(\"%s\");",
                    descriptor.getType()
                ),
                "do {"
            )
        );
        boolean extracted = false;
        if (descriptor.getData() instanceof UntypedHole) {
            extracted = true;
            code.addAll(
                Arrays.asList(
                    String.format(
                        "if (!builder.setData(extracted.getData(%d))) {",
                        ((UntypedHole) descriptor.getData()).getNumber()
                    ),
                    TransformationGenerator.BREAK,
                    "}"
                )
            );
        } else if (descriptor.getData() instanceof StaticString) {
            code.addAll(
                Arrays.asList(
                    String.format(
                        "if (!builder.setData(%s)) {",
                        ((StaticString) descriptor.getData()).toJavaCode()
                    ),
                    TransformationGenerator.BREAK,
                    "}"
                )
            );
        }
        final Pair<String, Boolean> children =
            this.generateChildren(klass, names, descriptor);
        code.add(children.getKey());
        extracted = extracted || children.getValue();
        code.addAll(
            Arrays.asList(
                "if (!builder.isValid()) {",
                TransformationGenerator.BREAK,
                "}"
            )
        );
        if (name.equals("root")) {
            method.addArgument(
                Strings.TYPE_FRAGMENT,
                "fragment",
                "Code fragment that is covered by the node being created"
            );
            code.add("builder.setFragment(fragment);");
        }
        code.addAll(
            Arrays.asList(
                "    result = builder.createNode();",
                "} while (false);",
                "return result;"
            )
        );
        if (extracted) {
            method.addArgument(
                "Extracted",
                "extracted",
                "Extracted nodes and data"
            );
        }
        method.setBody(String.join("\n", code));
        return new Pair<>(method, extracted);
    }

    /**
     * Creates a piece of code that populates the list of children of the node being created.
     * @param klass The class in which methods are created
     * @param names Method name generator
     * @param descriptor Node descriptor
     * @return Generated code lines with an indication whether to pass extracted data to it or not
     */
    private Pair<String, Boolean> generateChildren(final Klass klass,
        final NameGenerator names, final ResultingSubtreeDescriptor descriptor) {
        final List<String> code = new ArrayList<>(8);
        boolean extracted = false;
        if (descriptor.allChildrenAreHoles()) {
            extracted = true;
            final StringBuilder numbers = new StringBuilder();
            boolean flag = false;
            for (final RightSideItem item : descriptor.getChildren()) {
                if (!(item instanceof UntypedHole)) {
                    continue;
                }
                if (flag) {
                    numbers.append(", ");
                }
                flag = true;
                numbers.append(((UntypedHole) item).getNumber());
            }
            code.addAll(
                Arrays.asList(
                    String.format(
                        "final List<Node> children = extracted.getNodes(%s);",
                        numbers.toString()
                    ),
                    "if (!builder.setChildrenList(children)) {",
                    TransformationGenerator.BREAK,
                    "}"
                )
            );
        } else if (descriptor.getChildren().size() == 1) {
            this.collections = true;
            final Pair<Method, Boolean> child = this.generateBuilder(
                klass,
                names,
                (ResultingSubtreeDescriptor) descriptor.getChildren().get(0)
            );
            extracted = child.getValue();
            if (child.getValue()) {
                code.add(
                    String.format(
                        "final Node child = %s.%s(factory, extracted);",
                        klass.getName(),
                        child.getKey().getName()
                    )
                );
            } else {
                code.add(
                    String.format(
                        "final Node child = %s.%s(factory);",
                        klass.getName(),
                        child.getKey().getName()
                    )
                );
            }
            code.addAll(
                Arrays.asList(
                    "final List<Node> children = Collections.singletonList(child);",
                    "if (!builder.setChildrenList(children)) {",
                    TransformationGenerator.BREAK,
                    "}"
                )
            );
        }
        return new Pair<>(String.join("\n", code), extracted);
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
