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
import org.cqfn.astranaut.dsl.LeftDataDescriptor;
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
                this.item.toString()
            );
        } else if (data) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts data if matched",
                this.item.toString()
            );
        } else if (children) {
            brief = String.format(
                "Matches a node with the pattern '%s' and extracts nested nodes if matched",
                this.item.toString()
            );
        } else {
            brief = String.format(
                "Matches a node with the pattern '%s'",
                this.item.toString()
            );
        }
        final Klass klass = new Klass(context.generateClassName(), brief);
        LeftSideItemGenerator.generateInstanceAndConstructor(klass);
        this.generateCommonStaticFields(klass);
        this.generateMatchMethod(klass, context);
        return klass;
    }

    /**
     * Generates static fields containing the necessary information for matching and extraction.
     * @param klass The class to which the fields will be added
     */
    private void generateCommonStaticFields(final Klass klass) {
        final Field typename = new Field(
            Strings.TYPE_STRING,
            "TYPE_NAME",
            "Expected type name"
        );
        typename.makePrivate();
        typename.makeStatic();
        typename.makeFinal(String.format("\"%s\"", this.item.getType()));
        klass.addField(typename);
        final LeftDataDescriptor data = this.item.getData();
        if (data instanceof UntypedHole) {
            final Field hole = new Field(
                Strings.TYPE_INT,
                "DATA_HOLE",
                "Number of the cell into which the data is extracted"
            );
            hole.makePrivate();
            hole.makeStatic();
            hole.makeFinal(String.valueOf(((UntypedHole) data).getNumber()));
            klass.addField(hole);
        } else if (data instanceof StaticString) {
            final Field field = new Field(
                Strings.TYPE_STRING,
                "DATA",
                "Expected data"
            );
            field.makePrivate();
            field.makeStatic();
            field.makeFinal(((StaticString) data).toJavaCode());
            klass.addField(field);
        }
    }

    /**
     * Generates static fields to designate child indices and hole numbers for node extraction.
     * @param klass The class to which the fields will be added
     * @return Names of index fields in relation to hole fields
     */
    private List<Pair<String, String>> generateStaticFieldsForUntypedHoles(final Klass klass) {
        final List<Pair<String, String>> list = new ArrayList<>(0);
        final NameGenerator names = new NameGenerator();
        final List<PatternItem> children = this.item.getChildren();
        for (int index = 0; index < children.size(); index = index + 1) {
            final PatternItem child = children.get(index);
            if (child instanceof UntypedHole) {
                final String name = names.nextName();
                final String upper = name.toUpperCase(Locale.ENGLISH);
                final Field field = new Field(
                    Strings.TYPE_INT,
                    upper.concat("_INDEX"),
                    String.format("Index of the %s node to be extracted", name)
                );
                field.makePrivate();
                field.makeStatic();
                field.makeFinal(String.valueOf(index));
                klass.addField(field);
                final Field hole = new Field(
                    Strings.TYPE_INT,
                    upper.concat("_HOLE"),
                    String.format(
                        "Number of the cell into which the node at the %s index is extracted",
                        name
                    )
                );
                hole.makePrivate();
                hole.makeStatic();
                hole.makeFinal(String.valueOf(((UntypedHole) child).getNumber()));
                klass.addField(hole);
                list.add(new Pair<>(hole.getName(), field.getName()));
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
            final List<Pair<String, String>> holes =
                this.generateStaticFieldsForUntypedHoles(klass);
            if (this.item.getData() instanceof UntypedHole || !holes.isEmpty()) {
                final List<String> code = new ArrayList<>(
                    Arrays.asList(
                        String.format(
                            "final boolean matches = %s;",
                            this.composeCondition(klass, context)
                        ),
                        "if (matches) {"
                    )
                );
                if (this.item.getData() instanceof UntypedHole) {
                    code.add(
                        String.format(
                            "extracted.addData(%s.DATA_HOLE, node.getData());",
                            klass.getName()
                        )
                    );
                }
                for (final Pair<String, String> hole : holes) {
                    code.add(
                        String.format(
                            "extracted.addNode(%s.%s, node.getChild(%s.%s));",
                            klass.getName(),
                            hole.getKey(),
                            klass.getName(),
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
                    this.composeCondition(klass, context)
                )
            );
        } while (false);
    }

    /**
     * Composes a chain of conditions that check if a pattern has matched.
     * @param klass The class to which the {@code match} method will be added
     * @param context Generation context
     * @return Java boolean expression
     */
    private String composeCondition(final Klass klass, final LeftSideGenerationContext context) {
        final List<String> list = new ArrayList<>(1);
        final String name = klass.getName();
        list.add(String.format("node.belongsToGroup(%s.TYPE_NAME)", name));
        if (this.item.getData() instanceof StaticString) {
            list.add(String.format("node.getData().equals(%s.DATA)", name));
        }
        final List<PatternItem> children = this.item.getChildren();
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
