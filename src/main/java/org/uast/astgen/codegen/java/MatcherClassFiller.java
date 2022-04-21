/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Arrays;
import java.util.List;
import org.uast.astgen.rules.Data;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;

/**
 * Fills 'Matcher' classes (creates methods and fields).
 *
 * @since 1.0
 */
public class MatcherClassFiller {
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
    }

    /**
     * Fills the class.
     */
    public void fill() {
        this.klass.setInterfaces("Matcher");
        this.klass.makeSingleton();
        this.createStaticFields();
        this.createMatchMethod();
    }

    /**
     * Creates some static fields.
     */
    private void createStaticFields() {
        final Field type = new Field(
            "Expected node type",
            "String",
            "EXPECTED_TYPE"
        );
        type.makeStaticFinal();
        type.setInitExpr(String.format("\"%s\"", this.descriptor.getType()));
        this.klass.addField(type);
        final Field count = new Field(
            "Expected number of child nodes",
            "int",
            "EXPECTED_COUNT"
        );
        count.makeStaticFinal();
        count.setInitExpr(String.valueOf(this.descriptor.getParameters().size()));
        this.klass.addField(count);
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
        method.addArgument("Map<Integer, Node>", "children");
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
        final String common = String.format(
            "node.belongsToGroup(%s.EXPECTED_TYPE)\n\t&& node.getChildCount() == %s.EXPECTED_COUNT",
            name,
            name
        );
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
                final Hole hole = (Hole) parameter;
                extractor.append(
                    String.format(
                        "children.put(%d, node.getChild(%d));\n",
                        hole.getValue(),
                        index
                    )
                );
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
}
