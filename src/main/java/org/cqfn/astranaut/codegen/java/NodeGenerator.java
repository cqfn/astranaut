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

import java.util.Map;
import org.cqfn.astranaut.rules.Node;
import org.cqfn.astranaut.rules.Statement;

/**
 * Generates source code for rules that describe nodes.
 *
 * @since 0.1.5
 */
public class NodeGenerator {
    /**
     * The prepared environment collection required for generation,
     * one object for each programming language.
     */
    private final Map<String, Environment> envs;

    /**
     * Constructor.
     * @param envs The prepared environment collection required for generation
     */
    public NodeGenerator(final Map<String, Environment> envs) {
        this.envs = envs;
    }

    /**
     * Generates source code for node descriptor.
     * @param stmt The statement that contains node descriptor.
     * @return Compilation unit
     */
    public CompilationUnit generate(final Statement<Node> stmt) {
        final String language = stmt.getLanguage();
        final Node rule = stmt.getRule();
        final CompilationUnit unit;
        if (rule.isOrdinary()) {
            unit = new OrdinaryNodeGenerator(this.envs.get(language), stmt).generate();
        } else if (rule.isAbstract()) {
            unit = new AbstractNodeGenerator(this.envs.get(language), stmt).generate();
        } else if (rule.isList()) {
            unit = new ListNodeGenerator(this.envs.get(language), stmt).generate();
        } else {
            throw new IllegalStateException();
        }
        return unit;
    }
}
