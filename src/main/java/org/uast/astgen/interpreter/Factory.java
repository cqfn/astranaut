/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.Collections;

/**
 * The node factory.
 *
 * @since 1.0
 */
public final class Factory extends org.uast.astgen.base.Factory {
    /**
     * The instance.
     */
    public static final Factory INSTANCE = new Factory();

    /**
     * Constructor.
     */
    private Factory() {
        super(Collections.emptyMap());
    }
}
