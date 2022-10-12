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
package org.cqfn.astranaut.codegen.java;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.cqfn.astranaut.rules.Data;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.Hole;
import org.cqfn.astranaut.rules.HoleAttribute;
import org.cqfn.astranaut.rules.Parameter;
import org.cqfn.astranaut.rules.StringData;
import org.cqfn.astranaut.utils.LabelFactory;
import org.cqfn.astranaut.utils.StringUtils;

/**
 * Fills 'Matcher' classes (creates methods and fields).
 *
 * @since 0.1.5
 */
public class MatcherClassFiller {
    /**
     * The 'String' type name.
     */
    private static final String TYPE_STRING = "String";

    /**
     * The 'String' type name.
     */
    private static final String TYPE_INT = "int";

    /**
     * The '\"%s\"' format string.
     */
    private static final String STRING_IN_QUOTES = "\"%s\"";

    /**
     * The '_HOLE_ID' postfix.
     */
    private static final String HOLE_ID_POSTFIX = "_HOLE_ID";

    /**
     * The '_HOLE_TYPE' postfix.
     */
    private static final String HOLE_TYPE_POSTFIX = "_HOLE_TYPE";

    /**
     * The '_CHILD_ID' postfix.
     */
    private static final String CHILD_ID_POSTFIX = "_CHILD_ID";

    /**
     * The description of field that contains a hole number.
     */
    private static final String HOLE_NUM_DESCR = "The number of the %s hole";

    /**
     * The description of field that contains a hole type.
     */
    private static final String HOLE_TYPE_DESCR = "The type of the %s hole";

    /**
     * The description of field that contains a child index.
     */
    private static final String CHILD_ID_DESCR = "The index of the %s child";

    /**
     * Maximum number of predicates in one condition.
     */
    private static final int MAX_COND_COMPLEX = 4;

    /**
     * Descriptor of the children checker.
     */
    private static final String CHECKER_DESCR =
        "Checks if the children matches some structure, and extracts the data and children if so";

    /**
     * Type of children collection.
     */
    private static final String CHILDREN_TYPE = "Map<Integer, List<Node>>";

    /**
     * Name of children collection.
     */
    private static final String CHILDREN_NAME = "children";

    /**
     * Type of data collection.
     */
    private static final String DATA_TYPE = "Map<Integer, String>";

    /**
     * Name of data collection.
     */
    private static final String DATA_NAME = "data";

    /**
     * Type of node.
     */
    private static final String NODE_TYPE = "Node";

    /**
     * Name of node variable.
     */
    private static final String NODE_NAME = "node";

    /**
     * The 'boolean' type.
     */
    private static final String BOOLEAN_TYPE = "boolean";

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
     * Flag indicates that the package 'java.util.LinkedList' is needed.
     */
    private boolean llist;

    /**
     * The set of labels for naming holes.
     */
    private final LabelFactory holes;

    /**
     * The set of labels for naming children.
     */
    private final LabelFactory children;

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
        this.holes = new LabelFactory();
        this.children = new LabelFactory();
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
     * Returns the flag indicates that the package 'java.util.LinkedList' is needed.
     * @return The flag
     */
    public boolean isLinkedListNeeded() {
        return this.llist;
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
        if (!this.descriptor.hasEllipsisOrTypedHole()) {
            final Field count = new Field(
                "Expected number of child nodes",
                MatcherClassFiller.TYPE_INT,
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
        method.setReturnType(MatcherClassFiller.BOOLEAN_TYPE);
        method.addArgument(MatcherClassFiller.NODE_TYPE, MatcherClassFiller.NODE_NAME);
        method.addArgument(MatcherClassFiller.CHILDREN_TYPE, MatcherClassFiller.CHILDREN_NAME);
        method.addArgument(MatcherClassFiller.DATA_TYPE, MatcherClassFiller.DATA_NAME);
        final String condition = this.createCondition();
        if (this.descriptor.hasTypedHole()) {
            final String extractor = this.createExtractorWithTypedHoles();
            final String code = String.format(
                "boolean result = %s;\n%s\nreturn result;",
                condition,
                extractor
            );
            method.setCode(code);
        } else if (this.descriptor.hasHole()) {
            final String extractor = this.createExtractor();
            final List<String> code = Arrays.asList(
                String.format("final boolean result = %s;", condition),
                "if (result) {",
                extractor,
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
        int count = 1;
        String common = String.format("node.belongsToGroup(%s.EXPECTED_TYPE)", name);
        if (!this.descriptor.hasEllipsisOrTypedHole()) {
            common = common.concat(
                String.format("\n\t&& node.getChildCount() == %s.EXPECTED_COUNT", name)
            );
            count = count + 1;
        }
        condition.append(common);
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
            count = count + 1;
        }
        condition.append(this.createChildChecker(count));
        return condition.toString();
    }

    /**
     * Generates a checker of child nodes.
     * @param used Number of conditions that have already been generated
     * @return The expression (boolean type)
     */
    private String createChildChecker(final int used) {
        final String result;
        final List<Parameter> parameters = this.descriptor.getParameters();
        int count = 0;
        for (final Parameter parameter : parameters) {
            if (parameter instanceof Descriptor) {
                count = count + 1;
            }
        }
        if (count + used <= MatcherClassFiller.MAX_COND_COMPLEX) {
            final StringBuilder condition = new StringBuilder();
            int index = 0;
            for (final Parameter parameter : parameters) {
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
            result = condition.toString();
        } else {
            result = this.createChildCheckerMethod();
        }
        return result;
    }

    /**
     * Generates a method that checks of child nodes.
     * @return The expression (boolean type)
     */
    private String createChildCheckerMethod() {
        final Method method = new Method(
            MatcherClassFiller.CHECKER_DESCR,
            "matchChildren"
        );
        this.klass.addMethod(method);
        method.makePrivate();
        method.makeStatic();
        method.setReturnType(
            MatcherClassFiller.BOOLEAN_TYPE,
            "The result of matching, {@code true} if node matches and data was extracted"
        );
        method.addArgument(
            MatcherClassFiller.NODE_TYPE,
            MatcherClassFiller.NODE_NAME,
            "The node"
        );
        method.addArgument(
            MatcherClassFiller.CHILDREN_TYPE,
            MatcherClassFiller.CHILDREN_NAME,
            "Where to save children when matched"
        );
        method.addArgument(
            MatcherClassFiller.DATA_TYPE,
            MatcherClassFiller.DATA_NAME,
            "Where to save data when matched"
        );
        this.fillChildCheckerMethod(method);
        return String.format(
            "\n\t&& %s.matchChildren(node, children, data)",
            this.klass.getName()
        );
    }

    /**
     * Fills a method that checks of child nodes.
     * @param method Method
     */
    private void fillChildCheckerMethod(final Method method) {
        final List<String> code = new LinkedList<>();
        boolean first = true;
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Descriptor) {
                final String srclbl = this.children.getLabel();
                final String source = srclbl.toUpperCase(Locale.ENGLISH)
                    .concat(MatcherClassFiller.CHILD_ID_POSTFIX);
                this.createMagicNumber(
                    String.format(MatcherClassFiller.CHILD_ID_DESCR, srclbl),
                    source,
                    index
                );
                final String subclass = this.generator.generate((Descriptor) parameter);
                final String format =
                    "%s.INSTANCE.match(\n\tnode.getChild(%s.%s), children, data\n);";
                if (first) {
                    code.add(
                        String.format(
                            "boolean flag = ".concat(format),
                            subclass,
                            this.klass.getName(),
                            source
                        )
                    );
                    first = false;
                } else {
                    code.add(
                        String.format(
                            "flag = flag && ".concat(format),
                            subclass,
                            this.klass.getName(),
                            source
                        )
                    );
                }
            }
            index = index + 1;
        }
        code.add("return flag;");
        method.setCode(String.join("\n", code));
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
            final String label = this.holes.getLabel();
            final String destination = label.toUpperCase(Locale.ENGLISH)
                .concat(MatcherClassFiller.HOLE_ID_POSTFIX);
            this.createMagicNumber(
                String.format(MatcherClassFiller.HOLE_NUM_DESCR, label),
                destination,
                hole.getValue()
            );
            extractor.append(
                String.format(
                    "data.put(%s.%s, node.getData());\n",
                    this.klass.getName(),
                    destination
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
        final String dstlbl = this.holes.getLabel();
        final String destination = dstlbl.toUpperCase(Locale.ENGLISH)
            .concat(MatcherClassFiller.HOLE_ID_POSTFIX);
        this.createMagicNumber(
            String.format(MatcherClassFiller.HOLE_NUM_DESCR, dstlbl),
            destination,
            hole.getValue()
        );
        if (hole.getAttribute() == HoleAttribute.ELLIPSIS && index == 0) {
            result =
                String.format(
                    "children.put(%s.%s, node.getChildrenList());\n",
                    this.klass.getName(),
                    destination
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
                String.format(
                    "children.put(%s.%s, list);",
                    this.klass.getName(),
                    destination
                )
            );
            result = String.join("\n", code);
        } else {
            this.collections = true;
            final String srclbl = this.children.getLabel();
            final String source = srclbl.toUpperCase(Locale.ENGLISH)
                .concat(MatcherClassFiller.CHILD_ID_POSTFIX);
            this.createMagicNumber(
                String.format(MatcherClassFiller.CHILD_ID_DESCR, srclbl),
                source,
                index
            );
            final String format =
                "children.put(\n\t%s.%s,\n\tCollections.singletonList(node.getChild(%s.%s))\n);\n";
            result =
                String.format(
                    format,
                    this.klass.getName(),
                    destination,
                    this.klass.getName(),
                    source
                );
        }
        return result;
    }

    /**
     * Creates field that contains magic number.
     * @param brief The brief description
     * @param name The name
     * @param value The value
     */
    private void createMagicNumber(final String brief, final String name, final int value) {
        final Field field = new Field(brief, MatcherClassFiller.TYPE_INT, name);
        field.makeStaticFinal();
        field.setInitExpr(String.valueOf(value));
        this.klass.addField(field);
    }

    /**
     * Generates the code that extracts data or (and) children from the node
     * in case if descriptor has typed holes
     * @return Source code
     */
    private String createExtractorWithTypedHoles() {
        final StringBuilder extractor = new StringBuilder();
        llist = true;
        extractor.append("final LinkedList<Node> batch = new LinkedList<>(node.getChildrenList());\n");
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Hole) {
                extractor.append(this.formatIteratedHoleExtractor((Hole) parameter));
            }
            index = index + 1;
        }
        /*
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
            final String label = this.holes.getLabel();
            final String destination = label.toUpperCase(Locale.ENGLISH)
                .concat(MatcherClassFiller.HOLE_ID_POSTFIX);
            this.createMagicNumber(
                String.format(MatcherClassFiller.HOLE_NUM_DESCR, label),
                destination,
                hole.getValue()
            );
            extractor.append(
                String.format(
                    "data.put(%s.%s, node.getData());\n",
                    this.klass.getName(),
                    destination
                )
            );
        }

         */
        return extractor.toString();
    }

    /**
     * Formats string for the children extractor in case if hole index is unknown
     * @param hole The hole
     * @return Source code
     */
    private String formatIteratedHoleExtractor(final Hole hole) {
        final String dstlbl = this.holes.getLabel();
        final String destination = dstlbl.toUpperCase(Locale.ENGLISH)
            .concat(MatcherClassFiller.HOLE_ID_POSTFIX);
        this.createMagicNumber(
            String.format(MatcherClassFiller.HOLE_NUM_DESCR, dstlbl),
            destination,
            hole.getValue()
        );
        final List<String> code;
        if (hole.getAttribute() == HoleAttribute.ELLIPSIS) {
            code = Arrays.asList(
                "if (result) {",
                "final List<Node> list = new LinkedList<>();",
                "while (!batch.isEmpty()) {",
                "list.add(batch.pollFirst());",
                "}",
                String.format("children.put(%s.%s, list);", this.klass.getName(), destination),
                "}\n"
            );
        } else if (hole.getAttribute() == HoleAttribute.TYPED) {
            final String type = dstlbl.toUpperCase(Locale.ENGLISH)
                .concat(MatcherClassFiller.HOLE_TYPE_POSTFIX);
            final Field field = new Field(
                String.format(MatcherClassFiller.HOLE_TYPE_DESCR, dstlbl),
                MatcherClassFiller.TYPE_STRING,
                type
            );
            field.makeStaticFinal();
            field.setInitExpr(
                String.format(
                    MatcherClassFiller.STRING_IN_QUOTES,
                    hole.getType()
                )
            );
            this.klass.addField(field);
            code = Arrays.asList(
                "if (result) {",
                "final List<Node> list = new LinkedList<>();",
                "while (!batch.isEmpty()) {",
                "final Node child = batch.pollFirst();",
                String.format(
                    "if (%s.%s.equals(child.getTypeName())) {",
                    this.klass.getName(),
                    type
                ),
                "list.add(child);",
                "} else {",
                "batch.addFirst(child);",
                "break;",
                "}",
                "}",
                String.format("children.put(%s.%s, list);", this.klass.getName(), destination),
                "}\n"
            );
        } else {
            this.collections = true;
            code = Arrays.asList(
                "if (result && !batch.isEmpty()) {",
                "children.put(",
                String.format("\t%s.%s,", this.klass.getName(), destination),
                "\tCollections.singletonList(batch.pollFirst())",
                ");",
                "} else {",
                "result = false;",
                "}\n"
            );
        }
        return String.join("\n", code);
    }
}
