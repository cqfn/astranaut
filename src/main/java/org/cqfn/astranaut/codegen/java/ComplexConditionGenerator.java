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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternMatchingMode;
import org.cqfn.astranaut.dsl.TransformationDescriptor;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Generates code for a complex condition when some nodes are optional or repeated.
 *  In this case, auxiliary methods for each node are generated..
 * @since 1.0.0
 */
final class ComplexConditionGenerator implements ConditionGenerator  {
    /**
     * Piece of code inserted to break if the pattern is not matched.
     */
    private static final String NOT_MATCHED = "if (!matched) {\nbreak;\n}";

    /**
     * Transformation rule.
     */
    private final TransformationDescriptor rule;

    /**
     * Generation context.
     */
    private final Context context;

    /**
     * Class in which the code is generated, including additional methods.
     */
    private final Klass klass;

    /**
     * Set of matchers used in the condition.
     */
    private final Set<String> matchers;

    /**
     * Constructor.
     * @param rule Transformation rule
     * @param context Generation context
     * @param klass Class in which the code is generated, including additional methods
     */
    ComplexConditionGenerator(final TransformationDescriptor rule, final Context context,
        final Klass klass) {
        this.rule = rule;
        this.context = context;
        this.klass = klass;
        this.matchers = new TreeSet<>();
    }

    @Override
    public List<String> generate() {
        final List<String> code = new ArrayList<>(32);
        code.add("final Deque<Node> queue = new LinkedList<>(list.subList(index, list.size()));");
        code.add("final int size = queue.size();");
        final List<Pair<String, Boolean>> checkers = this.generateCheckers();
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
                    String.format("%s.%s(queue, extracted);", this.klass.getName(), pair.getKey())
                );
                continue;
            }
            if (count == 1) {
                code.addAll(
                    Arrays.asList(
                        String.format(
                            "final boolean matched = %s.%s(queue, extracted);",
                            this.klass.getName(),
                            pair.getKey()
                        ),
                        ComplexConditionGenerator.NOT_MATCHED
                    )
                );
                continue;
            }
            if (index == first) {
                code.add(
                    String.format(
                        "boolean matched = %s.%s(queue, extracted);",
                        this.klass.getName(),
                        pair.getKey()
                    )
                );
            } else {
                code.add(
                    String.format(
                        "matched = %s.%s(queue, extracted);",
                        this.klass.getName(),
                        pair.getKey()
                    )
                );
            }
            if (rest < 3) {
                code.add(ComplexConditionGenerator.NOT_MATCHED);
            }
            rest = rest - 1;
        }
        code.add("final int consumed = size - queue.size();");
        if (!(this.rule.getRight() instanceof UntypedHole)) {
            code.add(
                "final Fragment fragment = Fragment.fromNodes(list.subList(index, index + consumed));"
            );
        }
        return code;
    }

    @Override
    public Set<String> getMatchers() {
        return this.matchers;
    }

    /**
     * Generates checkers, that is, functions that take the next node and check it with a matcher.
     *  A complex condition consists of consecutive calls of such checkers.
     * @return List of generated checkers, specifying for each checker whether the checker
     *  returns a boolean value
     */
    private List<Pair<String, Boolean>> generateCheckers() {
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
            final String matcher = this.context.getMatchers().get(item.toString(false)).getName();
            this.matchers.add(matcher);
            if (mode == PatternMatchingMode.NORMAL) {
                method.setReturnsDescription(
                    "Matching result, {@code true} if the next node is matched to the pattern"
                );
                ComplexConditionGenerator.generateCheckerForNormalPattern(method, matcher, index);
            } else if (mode == PatternMatchingMode.OPTIONAL) {
                ComplexConditionGenerator.generateCheckerForOptionalPattern(method, matcher);
            } else {
                ComplexConditionGenerator.generateCheckerForRepeatedPattern(method, matcher);
            }
            this.klass.addMethod(method);
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
}
