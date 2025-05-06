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
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternDescriptor;
import org.cqfn.astranaut.dsl.PatternItem;
import org.cqfn.astranaut.dsl.PatternMatchingMode;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Generates a matcher class for matching patterns.
 * @since 1.0.0
 */
public final class PatternMatcherGenerator extends LeftSideItemGenerator {
    /**
     * Item for which the matcher is generated.
     */
    private final PatternDescriptor pattern;

    /**
     * Constructor.
     * @param pattern Item for which the matcher is generated
     */
    public PatternMatcherGenerator(final PatternDescriptor pattern) {
        this.pattern = pattern;
    }

    @Override
    public Klass generate(final LeftSideGenerationContext context) {
        final boolean data = this.pattern.getData() instanceof UntypedHole;
        final boolean children = !this.pattern.getChildren().isEmpty();
        final String brief;
        if (data && children) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts nested nodes and data if matched",
                this.pattern.toString(false)
            );
        } else if (data) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts data if matched",
                this.pattern.toString(false)
            );
        } else if (children) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts nested nodes if matched",
                this.pattern.toString(false)
            );
        } else {
            brief = String.format(
                "Matches a node with the pattern '%s'",
                this.pattern.toString(false)
            );
        }
        final Klass klass = new Klass(context.generateClassName(), brief);
        LeftSideItemGenerator.generateInstanceAndConstructor(klass);
        this.generateMatchMethod(klass, context);
        return klass;
    }

    /**
     * Gets the numbers of untyped holes and the indices of the corresponding nodes.
     * @return A list of pairs where key is the hole number and value is the node index
     */
    private List<Pair<Integer, Integer>> getNumbersOfUntypedHoles() {
        final List<Pair<Integer, Integer>> list = new ArrayList<>(0);
        final List<PatternItem> children = this.pattern.getChildren();
        for (int index = 0; index < children.size(); index = index + 1) {
            final PatternItem child = children.get(index);
            if (child instanceof UntypedHole) {
                list.add(new Pair<>(((UntypedHole) child).getNumber(), index));
            }
        }
        return list;
    }

    /**
     * Generates and adds a {@code match} method to the given class.
     * @param klass The class to which the {@code match} method will be added
     * @param context Generation context
     */
    private void generateMatchMethod(final Klass klass, final LeftSideGenerationContext context) {
        final Method method = new Method("boolean", "match");
        klass.addMethod(method);
        method.makePublic();
        method.addArgument("Node", "node");
        method.addArgument("Extracted", "extracted");
        do {
            if (this.pattern.hasOptionalOrRepeated()) {
                method.setBody(this.generateBodyWithComplexCondition(klass, context));
                break;
            }
            final List<Pair<Integer, Integer>> holes = this.getNumbersOfUntypedHoles();
            if (this.pattern.getData() instanceof UntypedHole || !holes.isEmpty()) {
                final List<String> code = new ArrayList<>(
                    Arrays.asList(
                        String.format(
                            "final boolean matches = %s;",
                            this.composeCondition(context)
                        ),
                        "if (matches) {"
                    )
                );
                if (this.pattern.getData() instanceof UntypedHole) {
                    code.add(
                        String.format(
                            "extracted.addData(%d, node.getData());",
                            ((UntypedHole) this.pattern.getData()).getNumber()
                        )
                    );
                }
                for (final Pair<Integer, Integer> hole : holes) {
                    code.add(
                        String.format(
                            "extracted.addNode(%d, node.getChild(%d));",
                            hole.getKey(),
                            hole.getValue()
                        )
                    );
                }
                code.add("}");
                code.add("return matches;");
                method.setBody(String.join("\n", code));
                break;
            }
            method.setBody(
                String.format(
                    "return %s;",
                    this.composeCondition(context)
                )
            );
        } while (false);
    }

    /**
     * Composes a chain of conditions that check if a pattern has matched.
     * @param context Generation context
     * @return Java boolean expression
     */
    private String composeCondition(final LeftSideGenerationContext context) {
        final List<String> list = new ArrayList<>(1);
        list.add(String.format("node.belongsToGroup(\"%s\")", this.pattern.getType()));
        final List<PatternItem> children = this.pattern.getChildren();
        list.add(String.format("node.getChildCount() == %d", children.size()));
        if (this.pattern.getData() instanceof StaticString) {
            list.add(
                String.format(
                    "node.getData().equals(%s)",
                    ((StaticString) this.pattern.getData()).toJavaCode()
                )
            );
        }
        for (int index = 0; index < children.size(); index = index + 1) {
            final PatternItem child = children.get(index);
            if (child instanceof LeftSideItem) {
                final Klass matcher = ((LeftSideItem) child).generateMatcher(context);
                list.add(
                    String.format(
                        "%s.INSTANCE.match(node.getChild(%d), extracted)",
                        matcher.getName(),
                        index
                    )
                );
            }
        }
        return String.join(" && ", list);
    }

    /**
     * Creates a {@code match} method body with a complex matching condition.
     *  Such a condition, for example, can be if a descriptor contains optional or repeating
     *  child descriptors.
     * @param klass The class to which the {@code match} method will be added
     * @param context Generation context
     * @return Body content as a string
     */
    private String generateBodyWithComplexCondition(final Klass klass,
        final LeftSideGenerationContext context) {
        context.addImport(klass, "java.util.Deque");
        context.addImport(klass, "java.util.LinkedList");
        final List<String> code = new ArrayList<>(8);
        code.add("boolean matches;");
        code.add("final Deque<Node> queue = new LinkedList<>(node.getChildrenList());");
        code.add("do {");
        final List<Pair<String, Boolean>> checkers = this.generateCheckers(klass, context);
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
            code.add(
                String.format(
                    "matches = %s.%s(queue, extracted);",
                    klass.getName(),
                    pair.getKey()
                )
            );
            if (rest < 3 && index < checkers.size() - 1) {
                code.addAll(
                    Arrays.asList(
                        "if (!matches) {",
                        "    break;",
                        "}"
                    )
                );
            }
            rest = rest - 1;
        }
        code.add("} while (false);");
        if (this.pattern.getData() instanceof UntypedHole) {
            code.addAll(
                Arrays.asList(
                    "if (matches) {",
                    String.format(
                        "extracted.addData(%s.HOLE_NUMBER, node.getData());",
                        klass.getName()
                    ),
                    "}"
                )
            );
        }
        code.add("return matches;");
        return String.join("\n", code);
    }

    /**
     * Generates checkers, that is, functions that take the next node and check it with a matcher.
     *  A complex condition consists of consecutive calls of such checkers.
     * @param klass The class to which checkers methods will be added
     * @param context Generation context
     * @return List of generated checkers, specifying for each checker whether the checker
     *  returns a boolean value
     */
    private List<Pair<String, Boolean>> generateCheckers(final Klass klass,
        final LeftSideGenerationContext context) {
        final List<PatternItem> children = this.pattern.getChildren();
        final List<Pair<String, Boolean>> checkers = new ArrayList<>(children.size());
        final NameGenerator names = new NameGenerator();
        for (int index = 0; index < children.size(); index = index + 1) {
            final String name = names.nextName();
            final PatternItem child = children.get(index);
            if (child instanceof UntypedHole) {
                final Pair<Method, Boolean> checker =
                    PatternMatcherGenerator.generateCheckerForUntypedHole(
                        name,
                        (UntypedHole) child,
                        index
                    );
                checkers.add(new Pair<>(checker.getKey().getName(), checker.getValue()));
                klass.addMethod(checker.getKey());
                continue;
            }
            final LeftSideItem item = (LeftSideItem) child;
            final PatternMatchingMode mode = item.getMatchingMode();
            final Method method = PatternMatcherGenerator.generateCheckerMethod(
                name,
                mode == PatternMatchingMode.NORMAL,
                child
            );
            checkers.add(new Pair<>(method.getName(), mode == PatternMatchingMode.NORMAL));
            final Klass matcher = item.generateMatcher(context);
            if (mode == PatternMatchingMode.NORMAL) {
                PatternMatcherGenerator.generateCheckerForNormalPattern(
                    method,
                    matcher.getName(),
                    index
                );
            } else if (mode == PatternMatchingMode.OPTIONAL) {
                PatternMatcherGenerator.generateCheckerForOptionalPattern(
                    method,
                    matcher.getName()
                );
            } else {
                PatternMatcherGenerator.generateCheckerForRepeatedPattern(
                    method,
                    matcher.getName()
                );
            }
            klass.addMethod(method);
        }
        return checkers;
    }

    /**
     * Generates a checker method with or without a return value.
     * @param name Method name
     * @param ret Is there a return value
     * @param item Pattern item
     * @return Generated method
     */
    private static Method generateCheckerMethod(final String name, final boolean ret,
        final PatternItem item) {
        final String type;
        if (ret) {
            type = Strings.TYPE_BOOLEAN;
        } else {
            type = Strings.TYPE_VOID;
        }
        final Method method = new Method(
            type,
            "check".concat(name.substring(0, 1).toUpperCase(Locale.ENGLISH))
                .concat(name.substring(1)),
            String.format(
                "Matches a node with the pattern '%s'",
                item.toString()
            )
        );
        method.makePrivate();
        method.makeStatic();
        method.addArgument("Deque<Node>", "queue", "Node queue");
        method.addArgument(
            "Extracted",
            "extracted",
            "Extracted nodes and data"
        );
        if (ret) {
            method.setReturnsDescription(
                "Matching result, {@code true} if the next node is matched to the pattern"
            );
        }
        return method;
    }

    /**
     * Generates a checker for an untyped hole.
     * @param name The checker name
     * @param hole Untyped hole
     * @param index Index of the pattern
     * @return Checker method and flag whether the checker returns a value
     */
    private static Pair<Method, Boolean> generateCheckerForUntypedHole(final String name,
        final UntypedHole hole, final int index) {
        final Method method = PatternMatcherGenerator.generateCheckerMethod(
            name,
            index > 0,
            hole
        );
        if (index == 0) {
            method.setBody(
                String.format("extracted.addNode(%d, queue.poll());", hole.getNumber())
            );
        } else {
            final List<String> code = Arrays.asList(
                "boolean result = false;",
                "if (!queue.isEmpty()) {",
                String.format("extracted.addNode(%d, queue.poll());", hole.getNumber()),
                "    result = true;",
                "}",
                "return result;"
            );
            method.setBody(String.join("\n", code));
        }
        return new Pair<>(method, index > 0);
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
