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

import java.util.List;
import java.util.Locale;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates source code for rules that describe abstract nodes.
 *
 * @since 0.1.5
 */
final class AbstractNodeGenerator extends BaseGenerator {
    /**
     * The DSL statement.
     */
    private final Instruction<Node> instruction;

    /**
     * Constructor.
     * @param env The environment required for generation.
     * @param instruction The DSL statement
     */
    AbstractNodeGenerator(final Environment env, final Instruction<Node> instruction) {
        super(env);
        this.instruction = instruction;
    }

    @Override
    public CompilationUnit generate() {
        final Environment env = this.getEnv();
        final Node rule = this.instruction.getRule();
        final String type = rule.getType();
        final Interface iface = new Interface(
            String.format("Node that describes the '%s' type", type),
            type
        );
        this.defineGettersForTaggedFields(iface);
        final String pkg = this.getPackageName(this.instruction.getLanguage());
        final CompilationUnit unit = new CompilationUnit(env.getLicense(), pkg, iface);
        final List<String> hierarchy = env.getHierarchy(type);
        if (hierarchy.size() > 1) {
            String ancestor = hierarchy.get(1);
            if (ancestor.equals(rule.getType())) {
                final String dir = env.getRootPackage().concat(".green.");
                ancestor = dir.concat(ancestor);
            }
            iface.setInterfaces(ancestor);
        } else {
            final String base = this.getEnv().getBasePackage();
            unit.addImport(base.concat(".Node"));
            iface.setInterfaces("Node");
        }
        return unit;
    }

    /**
     * Creates getter methods for tagged fields.
     * @param iface Where to create
     */
    private void defineGettersForTaggedFields(final Interface iface) {
        final List<TaggedChild> tags = this.getEnv().getTags(this.instruction.getRule().getType());
        for (final TaggedChild child : tags) {
            if (!child.isOverridden()) {
                final String type = child.getType();
                final String tag = child.getTag();
                final MethodDescriptor getter = new MethodDescriptor(
                    String.format("Returns the child with the '%s' tag", tag),
                    String.format(
                        "get%s%s",
                        tag.substring(0, 1).toUpperCase(Locale.ENGLISH),
                        tag.substring(1)
                    )
                );
                getter.setReturnType(type, "The node");
                iface.addMethod(getter);
            }
        }
    }
}
