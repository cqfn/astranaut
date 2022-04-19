/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.analyzer;

import java.util.LinkedList;

/**
 * The null results that is added to nodes that are not described in rules.
 *
 * @since 1.0
 */
public final class NullResult extends Result {
    /**
     * The instance.
     */
    public static final Result INSTANCE = new NullResult();

    /**
     * Constructor.
     */
    NullResult() {
        super(new LinkedList<>());
    }
}
