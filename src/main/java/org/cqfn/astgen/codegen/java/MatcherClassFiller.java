/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.cqfn.astgen.codegen.java;

import java.util.Arrays;
import java.util.List;
import org.cqfn.astgen.rules.Data;
import org.cqfn.astgen.rules.Descriptor;
import org.cqfn.astgen.rules.Hole;
import org.cqfn.astgen.rules.HoleAttribute;
import org.cqfn.astgen.rules.Parameter;
import org.cqfn.astgen.rules.StringData;
import org.cqfn.astgen.utils.StringUtils;

/**
 * Fills 'Matcher' classes (creates methods and fields).
 *
 * @since 1.0
 */
public class MatcherClassFiller {
    /**
     * The 'String' type name.
     */
    private static final String TYPE_STRING = "String";

    /**
     * The '\"%s\"' format string.
     */
    private static final String STRING_IN_QUOTES = "\"%s\"";

    /**
     * The generator.
     */
    private final MatcherGenerator generator;

    /**
     * Class to be filled.
     */
    private final Klass klass;

    /**
     * The descriptor.
     */
    private final Descriptor descriptor;

    /**
     * Flag indicates that the package 'java.util.Collections' is needed.
     */
    private boolean collections;

    /**
     * Flag indicates that the package 'java.util.ArrayList' is needed.
     */
    private boolean alist;

    /**
     * Constructor.
     * @param generator The generator
     * @param klass Class to be filled
     * @param descriptor The descriptor
     */
    public MatcherClassFiller(final MatcherGenerator generator, final Klass klass,
        final Descriptor descriptor) {
        this.generator = generator;
        this.klass = klass;
        this.descriptor = descriptor;
        this.collections = false;
        this.alist = false;
    }

    /**
     * Fills the class.
     */
    public void fill() {
        this.klass.makeFinal();
        this.klass.setInterfaces("Matcher");
        this.klass.makeSingleton();
        this.createStaticFields();
        this.createMatchMethod();
    }

    /**
     * Returns the flag indicates that the package 'java.util.Collections' is needed.
     * @return The flag
     */
    public boolean isCollectionsNeeded() {
        return this.collections;
    }

    /**
     * Returns the flag indicates that the package 'java.util.ArrayList' is needed.
     * @return The flag
     */
    public boolean isArrayListNeeded() {
        return this.alist;
    }

    /**
     * Creates some static fields.
     */
    private void createStaticFields() {
        final Field type = new Field(
            "Expected node type",
            MatcherClassFiller.TYPE_STRING,
            "EXPECTED_TYPE"
        );
        type.makeStaticFinal();
        type.setInitExpr(
            String.format(
                MatcherClassFiller.STRING_IN_QUOTES,
                this.descriptor.getType()
            )
        );
        this.klass.addField(type);
        if (!this.descriptor.hasEllipsisHole()) {
            final Field count = new Field(
                "Expected number of child nodes",
                "int",
                "EXPECTED_COUNT"
            );
            count.makeStaticFinal();
            count.setInitExpr(String.valueOf(this.descriptor.getParameters().size()));
            this.klass.addField(count);
        }
    }

    /**
     * Creates the 'match() method.
     */
    private void createMatchMethod() {
        final Method method = new Method("match");
        this.klass.addMethod(method);
        method.makeOverridden();
        method.setReturnType("boolean");
        method.addArgument("Node", "node");
        method.addArgument("Map<Integer, List<Node>>", "children");
        method.addArgument("Map<Integer, String>", "data");
        final String condition = this.createCondition();
        if (this.descriptor.hasHole()) {
            final List<String> code = Arrays.asList(
                String.format("final boolean result = %s;", condition),
                "if (result) {",
                this.createExtractor(),
                "}",
                "return result;"
            );
            method.setCode(String.join("\n", code));
        } else {
            method.setCode(String.format("return %s;", condition));
        }
    }

    /**
     * Generates the condition of the matcher.
     * @return The expression (boolean type)
     */
    private String createCondition() {
        final StringBuilder condition = new StringBuilder();
        final String name = this.klass.getName();
        String common = String.format("node.belongsToGroup(%s.EXPECTED_TYPE)", name);
        if (!this.descriptor.hasEllipsisHole()) {
            common = common.concat(
                String.format("\n\t&& node.getChildCount() == %s.EXPECTED_COUNT", name)
            );
        }
        condition.append(common);
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Descriptor) {
                final String subclass = this.generator.generate((Descriptor) parameter);
                condition.append(
                    String.format(
                        "\n\t&& %s.INSTANCE.match(node.getChild(%d), children, data)",
                        subclass,
                        index
                    )
                );
            }
            index = index + 1;
        }
        final Data data = this.descriptor.getData();
        if (data instanceof StringData) {
            final Field field = new Field(
                "Expected data",
                MatcherClassFiller.TYPE_STRING,
                "EXPECTED_DATA"
            );
            field.makeStaticFinal();
            field.setInitExpr(
                String.format(
                    MatcherClassFiller.STRING_IN_QUOTES,
                    new StringUtils(((StringData) data).getValue()).escapeEntities()
                )
            );
            this.klass.addField(field);
            condition.append(
                String.format(
                    "\n\t&& %s.EXPECTED_DATA.equals(node.getData())",
                    name
                )
            );
        }
        return condition.toString();
    }

    /**
     * Generates the code that extracts data or (and) children from the node.
     * @return Source code
     */
    private String createExtractor() {
        final StringBuilder extractor = new StringBuilder();
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Hole) {
                extractor.append(this.formatHoleExtractor((Hole) parameter, index));
            }
            index = index + 1;
        }
        final Data data = this.descriptor.getData();
        if (data instanceof Hole) {
            final Hole hole = (Hole) data;
            extractor.append(
                String.format(
                    "data.put(%d, node.getData());\n",
                    hole.getValue()
                )
            );
        }
        return extractor.toString();
    }

    /**
     * Formats string for the children extractor.
     * @param hole The hole
     * @param index The child index
     * @return Source code
     */
    private String formatHoleExtractor(final Hole hole, final int index) {
        final String result;
        if (hole.getAttribute() == HoleAttribute.ELLIPSIS && index == 0) {
            result =
                String.format(
                    "children.put(%d, node.getChildrenList());\n",
                    hole.getValue()
                );
        } else if (hole.getAttribute() == HoleAttribute.ELLIPSIS) {
            this.alist = true;
            final List<String> code = Arrays.asList(
                "final int count = node.getChildCount();",
                String.format("final List<Node> list = new ArrayList<>(count - %d);", index),
                String.format(
                    "for (int index = %d; index < count; index = index + 1) {",
                    index
                ),
                "list.add(node.getChild(index));",
                "}",
                String.format("children.put(%d, list);", hole.getValue())
            );
            result = String.join("\n", code);
        } else {
            this.collections = true;
            result =
                String.format(
                    "children.put(%d, Collections.singletonList(node.getChild(%d)));\n",
                    hole.getValue(),
                    index
                );
        }
        return result;
    }
}
