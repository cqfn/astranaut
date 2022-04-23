/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Arrays;
import java.util.List;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.utils.LabelFactory;

/**
 * Fills 'Matcher' classes (creates methods and fields).
 *
 * @since 1.0
 */
public class ConverterClassFiller {
    /**
     * The 'Node' type.
     */
    private static final String NODE_TYPE = "Node";

    /**
     * The 'Factory' type.
     */
    private static final String FACTORY_TYPE = "Factory";

    /**
     * The 'factory' variable name.
     */
    private static final String FACTORY_NAME = "factory";

    /**
     * The result node declaration.
      */
    private static final String DECLARE_RESULT = "Node result = EmptyTree.INSTANCE;";

    /**
     * Class to be filled.
     */
    private final Klass klass;

    /**
     * The root descriptor.
     */
    private final Descriptor root;

    /**
     * The name of the matcher class.
     */
    private final String matcher;

    /**
     * The generator for static strings.
     */
    private final StaticStringGenerator stg;

    /**
     * The factory for naming methods.
     */
    private final LabelFactory labels;

    /**
     * Constructor.
     * @param klass Class to be filled
     * @param descriptor The descriptor
     * @param matcher The name of the matcher class
     */
    public ConverterClassFiller(final Klass klass,
        final Descriptor descriptor, final String matcher) {
        this.klass = klass;
        this.root = descriptor;
        this.matcher = matcher;
        this.stg = new StaticStringGenerator(klass);
        this.labels = new LabelFactory();
    }

    /**
     * Fills the class.
     */
    public void fill() {
        this.klass.makeFinal();
        this.klass.setInterfaces("Converter");
        this.klass.makeSingleton();
        this.createConvertMethod();
    }

    /**
     * Creates the 'convert() method.
     */
    private void createConvertMethod() {
        final Method method = new Method("convert");
        this.klass.addMethod(method);
        method.makeOverridden();
        method.setReturnType(ConverterClassFiller.NODE_TYPE);
        method.addArgument(ConverterClassFiller.NODE_TYPE, "node");
        method.addArgument(ConverterClassFiller.FACTORY_TYPE, ConverterClassFiller.FACTORY_NAME);
        final List<String> code = Arrays.asList(
            ConverterClassFiller.DECLARE_RESULT,
            "final Map<Integer, Node> children = new TreeMap<>();",
            "final Map<Integer, String> data = new TreeMap<>();",
            String.format(
                "final boolean matched = %s.INSTANCE.match(node, children, data);",
                this.matcher
            ),
            "if (matched) {",
            String.format(
                "    result = %s.%s(factory, children, data);",
                this.klass.getName(),
                this.createBuildMethod(this.root)
            ),
            "}",
            "return result"
        );
        method.setCode(String.join("\n", code));
    }

    /**
     * Creates the 'xxxBuilder' method, where 'xxx' is 'first', 'second', etc.
     * @param descriptor The descriptor
     * @return The name of generated method
     */
    private String createBuildMethod(final Descriptor descriptor) {
        final String name = this.labels.getLabel().concat("Builder");
        final String type = descriptor.getType();
        final Method method = new Method(
            String.format("Builds a node with '%s' type", type),
            name
        );
        this.klass.addMethod(method);
        method.addArgument(
            ConverterClassFiller.FACTORY_TYPE,
            ConverterClassFiller.FACTORY_NAME,
            "The node factory"
        );
        final List<String> code = Arrays.asList(
            ConverterClassFiller.DECLARE_RESULT,
            String.format(
                "final Builder builder = factory.createBuilder(%s);",
                this.stg.getFieldName(type)
            ),
            "if (builder.isValid()) {",
            "    result = builder.createNode();",
            "}"
        );
        method.setCode(String.join("\n", code));
        method.addArgument(
            "Map<Integer, Node>",
            "children",
            "The collection of child nodes"
        );
        method.addArgument(
            "Map<Integer, String>",
            "data",
            "The data"
        );
        method.setReturnType(ConverterClassFiller.NODE_TYPE, "A node");
        return name;
    }
}
