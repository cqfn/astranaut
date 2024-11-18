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
package org.cqfn.astranaut.analyzer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.cqfn.astranaut.dsl.AbstractNodeDescriptor;
import org.cqfn.astranaut.dsl.NodeDescriptor;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Analyzer that analyzes an Astranaut program before code generation or execution
 *  and builds the necessary relationships between rules.
 * @since 1.0.0
 */
public class Analyzer {
    /**
     * DSL program.
     */
    private final Program program;

    /**
     * Constructor.
     * @param program DSL program
     */
    public Analyzer(final Program program) {
        this.program = program;
    }

    /**
     * Analyzes the DSL program and builds relationships between rules.
     * @throws BaseException If the DSL program contains errors
     */
    public void analyze() throws BaseException {
        final Set<String> languages = this.program.getAllLanguages();
        for (final String language : languages) {
            final List<NodeDescriptor> descriptors =
                this.program.getNodeDescriptorsForLanguage(language);
            Analyzer.linkAbstractNodes(descriptors);
        }
    }

    /**
     * Builds links between abstract and non-abstract nodes of the same language.
     * @param descriptors List of node descriptors
     * @throws BaseException If the DSL program contains errors
     */
    private static void linkAbstractNodes(final List<NodeDescriptor> descriptors)
        throws BaseException {
        final Map<String, NodeDescriptor> map = new TreeMap<>();
        for (final NodeDescriptor descriptor : descriptors) {
            map.put(descriptor.getName(), descriptor);
        }
        for (final NodeDescriptor descriptor : descriptors) {
            if (descriptor instanceof AbstractNodeDescriptor) {
                final AbstractNodeDescriptor abstrakt = (AbstractNodeDescriptor) descriptor;
                for (final String subtype : abstrakt.getSubtypes()) {
                    final NodeDescriptor inherited = map.get(subtype);
                    inherited.addBaseDescriptor(abstrakt);
                }
            }
        }
    }
}
