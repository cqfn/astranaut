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

import java.util.Set;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Rule;

/**
 * Generator that creates compilation units that describe a node or transformation.
 * @since 1.0.0
 */
public abstract class RuleGenerator {
    /**
     * Returns the rule for which the source code is generated.
     * @return Rule
     */
    public abstract Rule getRule();

    /**
     * Creates compilation units that describe a node or transformation.
     * @param context Data required to generate Java source code
     * @return Set of created compilation units (contains at least one)
     */
    public abstract Set<CompilationUnit> createUnits(Context context);

    /**
     * Resolves dependencies for the given compilation unit by adding the necessary imports.
     * @param unit The compilation unit to which imports are being added
     * @param context Data required to generate Java source code
     */
    protected void resolveDependencies(final CompilationUnit unit, final Context context) {
        final Rule rule = this.getRule();
        final String language = rule.getLanguage();
        final Set<NodeDescriptor> dependencies = rule.getDependencies();
        for (final NodeDescriptor descriptor : dependencies) {
            if (!descriptor.getLanguage().equals(language)) {
                final Package pkg = context
                    .getPackage()
                    .getParent()
                    .getParent()
                    .getSubpackage(descriptor.getLanguage(), "nodes");
                unit.addImport(String.format("%s.%s", pkg.toString(), descriptor.getName()));
            }
        }
    }
}
