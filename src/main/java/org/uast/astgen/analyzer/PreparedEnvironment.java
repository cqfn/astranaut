/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.uast.astgen.codegen.java.Environment;
import org.uast.astgen.codegen.java.License;
import org.uast.astgen.codegen.java.TaggedChild;
import org.uast.astgen.exceptions.GeneratorException;
import org.uast.astgen.rules.Statement;
import org.uast.astgen.rules.Vertex;

/**
 * Prepared environment, with preliminary analysis of the set of rules.
 *
 * @since 1.0
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
     * @param descriptors The list of descriptors
     * @param language The name of programming language that will limit a set of nodes
     * @throws GeneratorException If the environment can't be built for proposed rule set
     */
    public PreparedEnvironment(final Environment base, final List<Statement<Vertex>> descriptors,
        final String language) throws GeneratorException {
        this.base = base;
        this.language = language;
        this.analyzer = new Analyzer(descriptors, language).analyze();
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
    public String getBasePackage() {
        return this.base.getBasePackage();
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
        return this.analyzer.getHierarchy(name);
    }

    @Override
    public List<TaggedChild> getTags(final String type) {
        return new ArrayList<>(this.analyzer.getTags(type));
    }

    @Override
    public Set<String> getImports(final String type) {
        return this.analyzer.getImports(type);
    }
}
