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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.cqfn.astranaut.codegen.java.Environment;
import org.cqfn.astranaut.codegen.java.License;
import org.cqfn.astranaut.codegen.java.TaggedChild;
import org.cqfn.astranaut.exceptions.GeneratorException;

/**
 * Prepared environment, with preliminary analysis of the set of rules.
 *
 * @since 0.1.5
 */
public final class PreparedEnvironment implements Environment {
    /**
     * The base environment.
     */
    private final Environment base;

    /**
     * The name of programming language that will limit a set of nodes.
     */
    private final String language;

    /**
     * The analyzer to get additional data.
     */
    private final Analyzer analyzer;

    /**
     * Constructor.
     * @param base The base environment
     * @param analyzer The analyzer
     * @param language The name of programming language that will limit a set of nodes
     * @throws GeneratorException If the environment can't be built for proposed rule set
     */
    public PreparedEnvironment(final Environment base, final Analyzer analyzer,
        final String language) throws GeneratorException {
        this.base = base;
        this.language = language;
        this.analyzer = analyzer;
    }

    @Override
    public License getLicense() {
        return this.base.getLicense();
    }

    @Override
    public String getVersion() {
        return this.base.getVersion();
    }

    @Override
    public String getRootPackage() {
        return this.base.getRootPackage();
    }

    @Override
    public boolean isTestMode() {
        return this.base.isTestMode();
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Override
    public List<String> getHierarchy(final String name) {
        return this.analyzer.getHierarchy(name, this.language);
    }

    @Override
    public List<TaggedChild> getTags(final String type) {
        return new ArrayList<>(this.analyzer.getTags(type, this.language));
    }

    @Override
    public Set<String> getImports(final String type) {
        return this.analyzer.getImports(type, this.language);
    }

    @Override
    public boolean whetherToAddGeneratorVersion() {
        return this.base.whetherToAddGeneratorVersion();
    }
}
