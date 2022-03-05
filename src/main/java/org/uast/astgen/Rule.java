/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen;

import java.util.Map;
import java.util.Set;
import org.uast.astgen.exceptions.NotSpecifiedException;

/**
 * A rule that describes AST nodes or subtrees transformation.
 *
 * @since 1.0
 */
public interface Rule {
    @Override
    String toString();

    /**
     * Generates source code from this rule.
     * @param folder Name of the folder where to generate
     * @param pkg Name of the package where to generate
     * @param inheritance Node classes inheritance information
     * @param group Processed nodes information
     * @param today Current date generation requirement
     * @throws NotSpecifiedException If the node contains children
     *  not specified in a set of rules
     */
    void generate(String folder, String pkg,
        Map<String, Map<String, String>> inheritance, Map<String, Set<String>> group,
        boolean today) throws NotSpecifiedException;
}
