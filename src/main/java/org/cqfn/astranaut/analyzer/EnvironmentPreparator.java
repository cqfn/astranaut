/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.codegen.java.Environment;
import org.cqfn.astranaut.exceptions.GeneratorException;
import org.cqfn.astranaut.rules.Program;

/**
 * Prepares the collection of environments from the DSL program
 * which is required for generation of code.
 *
 * @since 0.2.3
 */
public class EnvironmentPreparator {
    /**
     * The set of DSL rules with addition data.
     */
    private final Program program;

    /**
     * The base environment implementation.
     */
    private final Environment base;

    /**
     * Constructor.
     * @param program The DSL program
     * @param base The environment
     */
    public EnvironmentPreparator(final Program program, final Environment base) {
        this.program = program;
        this.base = base;
    }

    /**
     * Prepares the environment collection required for generation of code.
     * @return The prepared environment collection required for generation,
     *  one object for each programming language
     * @throws GeneratorException if rules described in the DSL code
     *  contain mistakes
     */
    public Map<String, Environment> prepare() throws GeneratorException {
        final Map<String, Environment> env = new TreeMap<>();
        final VertexStorage storage = new VertexStorage(
            this.program.getVertices(), this.program.getNamesOfAllLanguages()
        );
        storage.collectAndCheck();
        final Analyzer analyzer = new Analyzer(storage);
        analyzer.analyzeGreen();
        for (final String language : this.program.getNamesOfAllLanguages()) {
            analyzer.analyze(language);
        }
        env.put("", new PreparedEnvironment(this.base, analyzer, ""));
        for (final String language : this.program.getNamesOfAllLanguages()) {
            env.put(
                language,
                new PreparedEnvironment(this.base, analyzer, language)
            );
        }
        return env;
    }
}
