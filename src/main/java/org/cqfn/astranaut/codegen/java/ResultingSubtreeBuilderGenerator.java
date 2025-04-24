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
import org.cqfn.astranaut.dsl.ResultingSubtreeDescriptor;
import org.cqfn.astranaut.dsl.RightSideItem;
import org.cqfn.astranaut.dsl.StaticString;
import org.cqfn.astranaut.dsl.UntypedHole;

/**
 * Generates the source code of the method that builds the resulting node (subtree)
 *  based on the right part of the transformation rule.
 * @since 1.0.0
 */
final class ResultingSubtreeBuilderGenerator {
    /**
     * Piece of code inserted to break.
     */
    private static final String BREAK = "break;";

    /**
     * Parent generator.
     */
    private ResultingSubtreeBuilderGenerator parent;

    /**
     * Class in which the method is generated.
     */
    private final Klass klass;

    /**
     * Method name generator.
     */
    private final NameGenerator names;

    /**
     * Various flags that are set during source code generation by the transformation rule.
     */
    private final TransformationGeneratorFlags flags;

    /**
     * Flag that indicates whether the 'extracted' parameter needs to be passed.
     */
    private boolean extracted;

    /**
     * Constructor.
     * @param klass Class in which the method is generated
     * @param names Method name generator
     * @param flags Various flags that are set during source code generation
     *  by the transformation rule
     */
    ResultingSubtreeBuilderGenerator(final Klass klass, final NameGenerator names,
        final TransformationGeneratorFlags flags) {
        this.klass = klass;
        this.names = names;
        this.flags = flags;
    }

    /**
     * Returns a flag indicating that the 'extracted' parameter should be passed
     *  for the generated method.
     * @return Flag
     */
    boolean isExtractedParameterNeeded() {
        return this.extracted;
    }

    /**
     * Generates a method that builds the resulting node (subtree) based on the right part
     *  of the transformation rule.
     * @param descriptor Descriptor on the basis of which the method is generated
     * @return Generated method
     */
    Method generate(final ResultingSubtreeDescriptor descriptor) {
        final String name = this.names.nextName();
        final Method method = new Method(
            Strings.TYPE_NODE,
            String.format(
                "build%s%s",
                name.substring(0, 1).toUpperCase(Locale.ENGLISH),
                name.substring(1)
            ),
            String.format(
                "Constructs a node based on the descriptor '%s'",
                descriptor.toString()
            )
        );
        this.klass.addMethod(method);
        method.makePrivate();
        method.makeStatic();
        method.addArgument(
            Strings.TYPE_FACTORY,
            "factory",
            "Factory for creating nodes"
        );
        method.setReturnsDescription("Created node");
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
        if (descriptor.getData() instanceof UntypedHole) {
            this.needExtracted();
            code.addAll(
                Arrays.asList(
                    String.format(
                        "if (!builder.setData(extracted.getData(%d))) {",
                        ((UntypedHole) descriptor.getData()).getNumber()
                    ),
                    ResultingSubtreeBuilderGenerator.BREAK,
                    "}"
                )
            );
        } else if (descriptor.getData() instanceof StaticString) {
            code.addAll(
                Arrays.asList(
                    String.format(
                        "if (!builder.setData(%s)) {",
                        ((StaticString) descriptor.getData()).toJavaCode()
                    ),
                    ResultingSubtreeBuilderGenerator.BREAK,
                    "}"
                )
            );
        }
        code.addAll(this.generateChildren(descriptor));
        code.addAll(
            Arrays.asList(
                "if (!builder.isValid()) {",
                ResultingSubtreeBuilderGenerator.BREAK,
                "}"
            )
        );
        if (this.parent == null) {
            method.addArgument(
                Strings.TYPE_FRAGMENT,
                "fragment",
                "Code fragment that is covered by the node being created"
            );
            code.add("builder.setFragment(fragment);");
        }
        code.addAll(
            Arrays.asList(
                "    result = builder.createNode();",
                "} while (false);",
                "return result;"
            )
        );
        if (this.extracted) {
            method.addArgument(
                "Extracted",
                "extracted",
                "Extracted nodes and data"
            );
        }
        method.setBody(String.join("\n", code));
        return method;
    }

    /**
     * Creates a piece of code that populates the list of children of the node being created.
     * @param descriptor Node descriptor
     * @return Generated code lines with an indication whether to pass extracted data to it or not
     */
    private List<String> generateChildren(final ResultingSubtreeDescriptor descriptor) {
        final List<String> code = new ArrayList<>(8);
        if (descriptor.allChildrenAreHoles()) {
            this.needExtracted();
            final StringBuilder numbers = new StringBuilder();
            boolean flag = false;
            for (final RightSideItem item : descriptor.getChildren()) {
                if (!(item instanceof UntypedHole)) {
                    continue;
                }
                if (flag) {
                    numbers.append(", ");
                }
                flag = true;
                numbers.append(((UntypedHole) item).getNumber());
            }
            code.addAll(
                Arrays.asList(
                    String.format(
                        "final List<Node> children = extracted.getNodes(%s);",
                        numbers.toString()
                    ),
                    "if (!builder.setChildrenList(children)) {",
                    ResultingSubtreeBuilderGenerator.BREAK,
                    "}"
                )
            );
        } else if (descriptor.getChildren().size() == 1) {
            this.flags.needCollections();
            final ResultingSubtreeBuilderGenerator gen = this.fork();
            final Method method = gen.generate(
                (ResultingSubtreeDescriptor) descriptor.getChildren().get(0)
            );
            if (gen.isExtractedParameterNeeded()) {
                code.add(
                    String.format(
                        "final Node child = %s.%s(factory, extracted);",
                        this.klass.getName(),
                        method.getName()
                    )
                );
            } else {
                code.add(
                    String.format(
                        "final Node child = %s.%s(factory);",
                        this.klass.getName(),
                        method.getName()
                    )
                );
            }
            code.addAll(
                Arrays.asList(
                    "final List<Node> children = Collections.singletonList(child);",
                    "if (!builder.setChildrenList(children)) {",
                    ResultingSubtreeBuilderGenerator.BREAK,
                    "}"
                )
            );
        }
        return code;
    }

    /**
     * Creates a new generator based on this one.
     * @return A new generator
     */
    private ResultingSubtreeBuilderGenerator fork() {
        final ResultingSubtreeBuilderGenerator obj = new ResultingSubtreeBuilderGenerator(
            this.klass,
            this.names,
            this.flags
        );
        obj.parent = this;
        return obj;
    }

    /**
     * Sets a flag indicating that the method is required to pass the 'extracted' parameter.
     */
    private void needExtracted() {
        this.extracted = true;
        if (this.parent != null) {
            this.parent.needExtracted();
        }
    }
}
