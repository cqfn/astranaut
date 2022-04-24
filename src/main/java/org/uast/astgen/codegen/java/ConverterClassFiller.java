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
import org.uast.astgen.rules.StringData;
import org.uast.astgen.utils.LabelFactory;
import org.uast.astgen.utils.StringUtils;

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
     * The result nod return.
     */
    private static final String RETURN_RESULT = "return result;";

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
        final CreationResult builder = this.createBuildMethod(this.root);
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
                "    result = %s.%s(%s);",
                this.klass.getName(),
                builder.getName(),
                builder.getArgumentsList()
            ),
            "}",
            ConverterClassFiller.RETURN_RESULT
        );
        method.setCode(String.join("\n", code));
    }

    /**
     * Creates the 'xxxBuilder' method, where 'xxx' is 'first', 'second', etc.
     * @param descriptor The descriptor
     * @return The result of creation
     */
    private CreationResult createBuildMethod(final Descriptor descriptor) {
        final String type = descriptor.getType();
        final String name = this.labels.getLabel().concat("Builder");
        final CreationResult result = new CreationResult(name);
        final Method method = this.createMethodObject(type, name);
        method.addArgument(
            ConverterClassFiller.FACTORY_TYPE,
            ConverterClassFiller.FACTORY_NAME,
            "The node factory"
        );
        final String children = this.processChildren(descriptor, result);
        final String data = ConverterClassFiller.processData(descriptor, result);
        final StringBuilder criteria = new StringBuilder(64);
        if (!children.isEmpty()) {
            criteria.append("applied && ");
        }
        if (!data.isEmpty()) {
            criteria.append("set && ");
        }
        criteria.append("builder.isValid()");
        final List<String> code = Arrays.asList(
            ConverterClassFiller.DECLARE_RESULT,
            String.format(
                "final Builder builder = factory.createBuilder(%s);",
                this.stg.getFieldName(type)
            ),
            children,
            data,
            String.format("if (%s) {", criteria.toString()),
            "    result = builder.createNode();",
            "}",
            ConverterClassFiller.RETURN_RESULT
        );
        method.setCode(String.join("\n", code));
        if (result.areChildrenNeeded()) {
            method.addArgument(
                "Map<Integer, Node>",
                "children",
                "The collection of child nodes"
            );
        }
        if (result.isDataNeeded()) {
            method.addArgument(
                "Map<Integer, String>",
                "data",
                "The data"
            );
        }
        return result;
    }

    /**
     * Creates method constructor.
     * @param type The name of type of created node
     * @param name The method name
     * @return The method constructor
     */
    private Method createMethodObject(final String type, final String name) {
        final Method method = new Method(
            String.format("Builds a node with '%s' type", type),
            name
        );
        method.makePrivate();
        method.makeStatic();
        method.setReturnType(ConverterClassFiller.NODE_TYPE, "A node");
        this.klass.addMethod(method);
        return method;
    }

    /**
     * Processes children, specified in descriptor.
     * @param descriptor The descriptor
     * @param crr The creation result (for flags modifying)
     * @return Source code
     */
    private String processChildren(final Descriptor descriptor, final CreationResult crr) {
        final StringBuilder code = new StringBuilder(128);
        code.append("final boolean applied = builder.setChildrenList(\n\tArrays.asList(\n");
        boolean flag = false;
        for (final Parameter parameter : descriptor.getParameters()) {
            if (flag) {
                code.append(",\n");
            }
            flag = true;
            if (parameter instanceof Hole) {
                final Hole hole = (Hole) parameter;
                code.append(String.format("\t\tchildren.get(%d)", hole.getValue()));
                crr.childrenNeeded();
            } else if (parameter instanceof Descriptor) {
                final Descriptor child = (Descriptor) parameter;
                final CreationResult builder = this.createBuildMethod(child);
                crr.merge(builder);
                code.append(
                    String.format(
                        "\t\t%s.%s(%s)",
                        this.klass.getName(),
                        builder.getName(),
                        builder.getArgumentsList()
                    )
                );
            }
        }
        final String result;
        if (flag) {
            result = code.append("\n\t)\n);").toString();
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Processes data, specified in descriptor.
     * @param descriptor The descriptor
     * @param crr The creation result (for flags modifying)
     * @return Source code
     */
    private static String processData(final Descriptor descriptor, final CreationResult crr) {
        String code = "";
        final Data data = descriptor.getData();
        if (data instanceof Hole) {
            final Hole hole = (Hole) data;
            code = String.format(
                "final boolean set = builder.setData(data.get(%s));",
                hole.getValue()
            );
            crr.dataNeeded();
        } else if (data instanceof StringData) {
            final StringData string = (StringData) data;
            code = String.format(
                "final boolean set = builder.setData(\"%s\");",
                new StringUtils(string.getValue()).escapeEntities()
            );
        }
        return code;
    }

    /**
     * The result of the {@link ConverterClassFiller#createConvertMethod()} method.
     * @since 1.0
     */
    private static class CreationResult {
        /**
         * The name of the created method.
         */
        private final String name;

        /**
         * Flag indicating that the method has the 'children' argument.
         */
        private boolean children;

        /**
         * Flag indicating that the method has the 'data' argument.
         */
        private boolean data;

        /**
         * Constructor.
         * @param name The name of the created method
         */
        CreationResult(final String name) {
            this.name = name;
            this.children = false;
            this.data = false;
        }

        /**
         * Sets the flag to indicate that the 'children' argument is required.
         */
        void childrenNeeded() {
            this.children = true;
        }

        /**
         * Sets the flag to indicate that the 'data' argument is required.
         */
        void dataNeeded() {
            this.data = true;
        }

        /**
         * Performs flag merging with another object.
         * @param other Another object
         */
        void merge(final CreationResult other) {
            this.children = this.children | other.children;
            this.data = this.data | other.data;
        }

        /**
         * Returns the name of the created method.
         * @return The name
         */
        String getName() {
            return this.name;
        }

        /**
         * Returns the flag to indicate that the 'children' argument is required.
         * @return The flag
         */
        boolean areChildrenNeeded() {
            return this.children;
        }

        /**
         * Returns the flag to indicate that the 'data' argument is required.
         * @return The flag
         */
        boolean isDataNeeded() {
            return this.data;
        }

        /**
         * Returns the list of arguments required to call the generated method.
         * @return The list as a string
         */
        String getArgumentsList() {
            final StringBuilder builder = new StringBuilder()
                .append(ConverterClassFiller.FACTORY_NAME);
            if (this.children) {
                builder.append(", children");
            }
            if (this.data) {
                builder.append(", data");
            }
            return builder.toString();
        }
    }
}
