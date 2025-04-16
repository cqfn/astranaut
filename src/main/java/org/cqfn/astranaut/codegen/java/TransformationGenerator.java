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
import java.util.TreeSet;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.dsl.LeftSideItem;
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
@SuppressWarnings("PMD.TooManyMethods")
public final class TransformationGenerator extends RuleGenerator {
    /**
     * Piece of code inserted to break if the pattern is not matched.
     */
    private static final String NOT_MATCHED = "if (!matched) {\nbreak;\n}";

    /**
     * Transformation rule.
     */
    private final TransformationDescriptor rule;

    /**
     * Minimum number of nodes consumed by the rule.
     */
    private final int consumed;

    /**
     * The condition of the rule is complex.
     */
    private final boolean complex;

    /**
     * Constructor.
     * @param rule The transformation rule from which the source code is generated
     */
    public TransformationGenerator(final TransformationDescriptor rule) {
        this.rule = rule;
        this.consumed = rule.getMinConsumed();
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
        final Set<String> matchers = new TreeSet<>();
        this.createConvertMethod(context, klass, matchers);
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
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.ConversionResult");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Converter");
        unit.addImport("org.cqfn.astranaut.core.algorithms.conversion.Extracted");
        unit.addImport("org.cqfn.astranaut.core.base.Factory");
        unit.addImport("org.cqfn.astranaut.core.base.Node");
        unit.addImport("org.cqfn.astranaut.core.base.DummyNode");
        unit.addImport("org.cqfn.astranaut.core.base.Builder");
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
        if (this.rule.getLeft().size() == 1
            && this.rule.getLeft().get(0).getMatchingMode() == PatternMatchingMode.REPEATED) {
            this.generateConditionForRepeatedNode(context, code, matchers);
        } else if (this.complex) {
            final List<Pair<String, Boolean>> checkers =
                this.generateCheckers(context, klass, matchers);
            TransformationGenerator.generateComplexCondition(klass, code, checkers);
        } else {
            this.generateSimpleCondition(context, code, matchers);
        }
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
        } else if (this.rule.getRight() instanceof ResultingSubtreeDescriptor) {
            TransformationGenerator.generateBuilder(
                klass,
                new NameGenerator("root"),
                (ResultingSubtreeDescriptor) this.rule.getRight()
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
     * Generates code for a simple condition, that is, when there are one or more patterns,
     *  and each pattern is matched by one node.
     * @param context Context with available matchers
     * @param code List of source code lines where the generated condition is added
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
        code.add(TransformationGenerator.NOT_MATCHED);
    }

    /**
     * Generates code for a degenerate condition where one pattern must have one or more nodes
     *  matched to it.
     * @param context Context with available matchers
     * @param code List of source code lines where the generated condition is added
     * @param matchers Set to collect used matcher names
     */
    private void generateConditionForRepeatedNode(final Context context, final List<String> code,
        final Set<String> matchers) {
        final LeftSideItem item = this.rule.getLeft().get(0);
        final String matcher = context.getMatchers().get(item.toString(false)).getName();
        matchers.add(matcher);
        code.addAll(
            Arrays.asList(
                "int consumed = 0;",
                "for (int offset = 0; index + offset < list.size(); offset = offset + 1) {",
                "    final Node node = list.get(index + offset);",
                String.format("    if (!%s.INSTANCE.match(node, extracted)) {", matcher),
                "        break;",
                "    }",
                "    consumed = consumed + 1;",
                "}",
                "if (consumed == 0) {",
                "    break;",
                "}"
            )
        );
    }

    /**
     * Generates checkers, that is, functions that take the next node and check it with a matcher.
     *  A complex condition consists of consecutive calls of such checkers.
     * @param context Context
     * @param klass Converter class
     * @param matchers Set to collect used matcher names
     * @return List of generated checkers, specifying for each checker whether the checker
     *  returns a boolean value
     */
    private List<Pair<String, Boolean>> generateCheckers(final Context context, final Klass klass,
        final Set<String> matchers) {
        final List<LeftSideItem> items = this.rule.getLeft();
        final List<Pair<String, Boolean>> checkers = new ArrayList<>(items.size());
        final NameGenerator names = new NameGenerator();
        for (int index = 0; index < items.size(); index = index + 1) {
            final LeftSideItem item = items.get(index);
            final String name = names.nextName();
            final PatternMatchingMode mode = item.getMatchingMode();
            final String ret;
            if (mode == PatternMatchingMode.NORMAL) {
                ret = Strings.TYPE_BOOLEAN;
            } else {
                ret = Strings.TYPE_VOID;
            }
            final Method method = new Method(
                ret,
                String.format(
                    "check%s%s",
                    name.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    name.substring(1)
                ),
                String.format(
                    "Matches a node with the pattern '%s'",
                    item.toString(true)
                )
            );
            checkers.add(new Pair<>(method.getName(), ret.equals(Strings.TYPE_BOOLEAN)));
            method.makePrivate();
            method.makeStatic();
            method.addArgument("Deque<Node>", "queue", "Node queue");
            method.addArgument(
                "Extracted",
                "extracted",
                "Extracted nodes and data"
            );
            final String matcher = context.getMatchers().get(item.toString(false)).getName();
            matchers.add(matcher);
            if (mode == PatternMatchingMode.NORMAL) {
                method.setReturnsDescription(
                    "Matching result, {@code true} if the next node is matched to the pattern"
                );
                TransformationGenerator.generateCheckerForNormalPattern(method, matcher, index);
            } else if (mode == PatternMatchingMode.OPTIONAL) {
                TransformationGenerator.generateCheckerForOptionalPattern(method, matcher);
            } else {
                TransformationGenerator.generateCheckerForRepeatedPattern(method, matcher);
            }
            klass.addMethod(method);
        }
        return checkers;
    }

    /**
     * Generates a checker for a “normal” pattern (not repetitive or optional).
     * @param method Checker method
     * @param matcher Matcher used inside the checker
     * @param index Index of the pattern
     */
    private static void generateCheckerForNormalPattern(final Method method, final String matcher,
        final int index) {
        final List<String> code;
        if (index == 0) {
            code = Arrays.asList(
                "final Node node = queue.poll();",
                String.format("return %s.INSTANCE.match(node, extracted);", matcher)
            );
        } else {
            code = Arrays.asList(
                "boolean result = false;",
                "if (!queue.isEmpty()) {",
                "    final Node node = queue.poll();",
                String.format("result = %s.INSTANCE.match(node, extracted);", matcher),
                "}",
                "return result;"
            );
        }
        method.setBody(String.join("\n", code));
    }

    /**
     * Generates a checker for an optional pattern.
     * @param method Checker method
     * @param matcher Matcher used inside the checker
     */
    private static void generateCheckerForOptionalPattern(final Method method,
        final String matcher) {
        final List<String> code = Arrays.asList(
            "if (queue.isEmpty()) {",
            "    return;",
            "}",
            "final Node node = queue.poll();",
            String.format("final boolean matched = %s.INSTANCE.match(node, extracted);", matcher),
            "if (!matched) {",
            "    queue.addFirst(node);",
            "}"
        );
        method.setBody(String.join("\n", code));
    }

    /**
     * Generates a checker for a repeating pattern.
     * @param method Checker method
     * @param matcher Matcher used inside the checker
     */
    private static void generateCheckerForRepeatedPattern(final Method method,
        final String matcher) {
        final List<String> code = Arrays.asList(
            "while (!queue.isEmpty()) {",
            "    final Node node = queue.poll();",
            String.format("final boolean matched = %s.INSTANCE.match(node, extracted);", matcher),
            "    if (!matched) {",
            "        queue.addFirst(node);",
            "        break;",
            "    }",
            "}"
        );
        method.setBody(String.join("\n", code));
    }

    /**
     * Generates code for a complex condition when there are optional and repeating rules.
     * @param klass Converter class
     * @param code Code lines where the generated condition is added
     * @param checkers List of checker function names generated earlier
     */
    private static void generateComplexCondition(final Klass klass, final List<String> code,
        final List<Pair<String, Boolean>> checkers) {
        code.add("final Deque<Node> queue = new LinkedList<>(list.subList(index, list.size()));");
        code.add("final int size = queue.size();");
        int count = 0;
        int first = -1;
        for (int index = 0; index < checkers.size(); index = index + 1) {
            final Pair<String, Boolean> pair = checkers.get(index);
            if (pair.getValue()) {
                if (first < 0) {
                    first = index;
                }
                count = count + 1;
            }
        }
        int rest = count;
        for (int index = 0; index < checkers.size(); index = index + 1) {
            final Pair<String, Boolean> pair = checkers.get(index);
            if (!pair.getValue()) {
                code.add(
                    String.format("%s.%s(queue, extracted);", klass.getName(), pair.getKey())
                );
                continue;
            }
            if (count == 1) {
                code.addAll(
                    Arrays.asList(
                        String.format(
                            "final boolean matched = %s.%s(queue, extracted);",
                            klass.getName(),
                            pair.getKey()
                        ),
                        TransformationGenerator.NOT_MATCHED
                    )
                );
                continue;
            }
            if (index == first) {
                code.add(
                    String.format(
                        "boolean matched = %s.%s(queue, extracted);",
                        klass.getName(),
                        pair.getKey()
                    )
                );
            } else {
                code.add(
                    String.format(
                        "matched = %s.%s(queue, extracted);",
                        klass.getName(),
                        pair.getKey()
                    )
                );
            }
            if (rest < 3) {
                code.add(TransformationGenerator.NOT_MATCHED);
            }
            rest = rest - 1;
        }
        code.add("final int consumed = size - queue.size();");
    }

    /**
     * Creates a function that creates a node based on its descriptor,
     *  using the extracted nodes and data.
     * @param klass The class in which the method is created.
     * @param names Method name generator
     * @param descriptor Node descriptor
     * @return Created method
     */
    private static Method generateBuilder(final Klass klass, final NameGenerator names,
        final ResultingSubtreeDescriptor descriptor) {
        final String name = names.nextName();
        final Method method = new Method(
            Strings.TYPE_NODE,
            String.format(
                "build%s%s",
                name.substring(0, 1).toUpperCase(Locale.ENGLISH),
                name.substring(1)
            ),
            String.format(
                "Constructs a method based on the descriptor '%s'",
                descriptor.toString()
            )
        );
        method.makePrivate();
        method.makeStatic();
        method.addArgument(
            "Extracted",
            "extracted",
            "Extracted nodes and data"
        );
        method.addArgument(
            Strings.TYPE_FACTORY,
            "factory",
            "Factory for creating nodes"
        );
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
        code.addAll(
            Arrays.asList(
                "    if (!builder.isValid()) {",
                "         break;",
                "    }",
                "    result = builder.createNode();",
                "} while (false);",
                "return result;"
            )
        );
        method.setBody(String.join("\n", code));
        klass.addMethod(method);
        return method;
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
