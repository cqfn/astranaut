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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.ChildDescriptorExt;
import org.cqfn.astranaut.dsl.Rule;

/**
 * Generator that creates compilation units that describe an abstract node.
 * @since 1.0.0
 */
public final class AbstractNodeGenerator extends RuleGenerator {
    /**
     * Descriptor on the basis of which the source code will be built.
     */
    private final AbstractNodeDescriptor rule;

    /**
     * Constructor.
     * @param rule Descriptor on the basis of which the source code will be built
     */
    public AbstractNodeGenerator(final AbstractNodeDescriptor rule) {
        this.rule = rule;
    }

    @Override
    public Rule getRule() {
        return this.rule;
    }

    @Override
    public Set<CompilationUnit> createUnits(final Context context) {
        final String name = this.rule.getName();
        final String brief = String.format("Node of the '%s' type.", name);
        final Interface iface = new Interface(name, brief);
        iface.makePublic();
        final List<AbstractNodeDescriptor> bases = this.rule.getBaseDescriptors();
        boolean node = false;
        if (bases.isEmpty()) {
            node = true;
            iface.setExtendsList(Strings.TYPE_NODE);
        } else {
            iface.setExtendsList(
                bases.stream()
                    .map(AbstractNodeDescriptor::getName)
                    .toArray(String[]::new)
            );
        }
        iface.setVersion(context.getVersion());
        this.createGettersForTaggedFields(iface);
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            iface
        );
        if (node) {
            final String base = "org.cqfn.astranaut.core.base.";
            unit.addImport(base.concat(Strings.TYPE_NODE));
        }
        this.resolveDependencies(unit, context);
        return Collections.singleton(unit);
    }

    /**
     * Creates getters for all tagged children.
     * @param iface Interface describing abstract node
     */
    private void createGettersForTaggedFields(final Interface iface) {
        final Map<String, ChildDescriptorExt> tags = this.rule.getTags();
        for (final Map.Entry<String, ChildDescriptorExt> entry : tags.entrySet()) {
            final String tag = entry.getKey();
            if (this.rule.baseHasTag(tag)) {
                continue;
            }
            final MethodSignature getter = new MethodSignature(
                entry.getValue().getType(),
                String.format(
                    "get%s%s",
                    tag.substring(0, 1).toUpperCase(Locale.ENGLISH),
                    tag.substring(1)
                ),
                String.format("Returns child node with '%s' tag", tag)
            );
            if (entry.getValue().isOptional()) {
                getter.setReturnsDescription(
                    String.format(
                        "Child node or {@code null} if the node with '%s' tag is not specified",
                        tag
                    )
                );
            } else {
                getter.setReturnsDescription("Child node (can't be {@code null})");
            }
            iface.addMethodSignature(getter);
        }
    }
}
