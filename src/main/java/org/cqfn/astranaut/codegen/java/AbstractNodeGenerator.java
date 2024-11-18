/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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
import java.util.Set;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;

/**
 * Generator that creates compilation units that describe an abstract node.
 * @since 1.0.0
 */
public final class AbstractNodeGenerator implements  RuleGenerator {
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
        final CompilationUnit unit = new CompilationUnit(
            context.getLicense(),
            context.getPackage(),
            iface
        );
        if (node) {
            final String base = "org.cqfn.astranaut.core.base.";
            unit.addImport(base.concat(Strings.TYPE_NODE));
        }
        return Collections.singleton(unit);
    }
}
