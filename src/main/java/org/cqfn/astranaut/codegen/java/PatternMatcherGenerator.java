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
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.dsl.LeftSideItem;
import org.cqfn.astranaut.dsl.PatternDescriptor;
import org.cqfn.astranaut.dsl.PatternItem;
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
    private final PatternDescriptor item;

    /**
     * Constructor.
     * @param item Item for which the matcher is generated
     */
    public PatternMatcherGenerator(final PatternDescriptor item) {
        this.item = item;
    }

    @Override
    public Klass generate(final LeftSideGenerationContext context) {
        final boolean data = this.item.getData() instanceof UntypedHole;
        final boolean children = !this.item.getChildren().isEmpty();
        final String brief;
        if (data && children) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts nested nodes and data if matched",
                this.item.toString(false)
            );
        } else if (data) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts data if matched",
                this.item.toString(false)
            );
        } else if (children) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts nested nodes if matched",
                this.item.toString(false)
            );
        } else {
            brief = String.format(
                "Matches a node with the pattern '%s'",
                this.item.toString(false)
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
        final List<PatternItem> children = this.item.getChildren();
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
            if (this.item.hasOptionalOrRepeated()) {
                method.setBody(this.generateBodyWithComplexCondition(klass));
                break;
            }
            final List<Pair<Integer, Integer>> holes = this.getNumbersOfUntypedHoles();
            if (this.item.getData() instanceof UntypedHole || !holes.isEmpty()) {
                final List<String> code = new ArrayList<>(
                    Arrays.asList(
                        String.format(
                            "final boolean matches = %s;",
                            this.composeCondition(context)
                        ),
                        "if (matches) {"
                    )
                );
                if (this.item.getData() instanceof UntypedHole) {
                    code.add(
                        String.format(
                            "extracted.addData(%d, node.getData());",
                            ((UntypedHole) this.item.getData()).getNumber()
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
        list.add(String.format("node.belongsToGroup(\"%s\")", this.item.getType()));
        final List<PatternItem> children = this.item.getChildren();
        list.add(String.format("node.getChildCount() == %d", children.size()));
        if (this.item.getData() instanceof StaticString) {
            list.add(
                String.format(
                    "node.getData().equals(%s)",
                    ((StaticString) this.item.getData()).toJavaCode()
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
     * @return Body content as a string
     */
    private String generateBodyWithComplexCondition(final Klass klass) {
        final List<String> code = new ArrayList<>(2);
        if (this.item.getData() instanceof UntypedHole) {
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
}
