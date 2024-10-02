/*
 * Copyright (c) 2024 John Doe
 */
package org.cqfn.astranaut.test;

import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;

/**
 * Node of the 'This' type.
 * @since 1.0.0
 */
public class This implements Node {
    /**
     * Type implementation describing 'This' nodes.
     * @since 1.0.0
     */
    private static final class ThisType implements Type {
    }

    /**
     * Constructor (builder) that creates nodes of the 'This' type.
     * @since 1.0.0
     */
    public static final class Constructor implements Builder {
    }
}
