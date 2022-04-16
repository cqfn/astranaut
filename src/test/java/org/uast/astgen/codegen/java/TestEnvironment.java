/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.codegen.java;

import java.util.Collections;
import java.util.List;

/**
 * Environment for test purposes.
 *
 * @since 1.0
 */
final class TestEnvironment implements Environment {
    /**
     * The license.
     */
    private final License license;

    /**
     * Constructor.
     */
    TestEnvironment() {
        this.license = new License("LICENSE_header.txt");
    }

    @Override
    public License getLicense() {
        return this.license;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getRootPackage() {
        return "org.uast.example";
    }

    @Override
    public String getBasePackage() {
        return "org.uast.uast.base";
    }

    @Override
    public boolean isTestMode() {
        return false;
    }

    @Override
    public List<String> getHierarchy(final String name) {
        return Collections.singletonList(name);
    }
}
