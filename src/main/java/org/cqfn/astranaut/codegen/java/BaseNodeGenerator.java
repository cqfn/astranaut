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

import java.util.List;
import org.cqfn.astranaut.rules.Node;

/**
 * Generates source code for rules that describe nodes.
 *
 * @since 0.1.5
 */
public abstract class BaseNodeGenerator extends BaseGenerator {
    /**
     * Constructor.
     * @param env The environment required for generation.
     */
    BaseNodeGenerator(final Environment env) {
        super(env);
    }

    /**
     * Creates class for node construction.
     * @param rule The rule
     * @return The class constructor
     */
    protected Klass createClass(final Node rule) {
        final String type = rule.getType();
        final Klass klass = new Klass(
            String.format("Node that describes the '%s' type", type),
            type
        );
        if (this.getEnv().whetherToAddGeneratorVersion()) {
            klass.addGeneratorVersion();
        }
        klass.makeFinal();
        final List<String> hierarchy = this.getEnv().getHierarchy(type);
        if (hierarchy.size() > 1) {
            klass.setInterfaces(hierarchy.get(1));
        } else {
            klass.setInterfaces("Node");
        }
        return klass;
    }
}
