/*
 * Copyright (c) 2024 John Doe
 */
package org.cqfn.astranaut.test;

import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.Fragment;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.core.base.Type;

/**
 * Node of the 'This' type.
 * @since 1.0.0
 */
public final class This implements Node {
    /**
     * Name of the type.
     */
    public static final String NAME = "This";

    /**
     * Type of the node.
     */
    public static final Type TYPE = new ThisType();

    /**
     * Fragment of source code that is associated with the node.
     */
    private Fragment fragment;

    @Override
    public Fragment getFragment() {
        return this.fragment;
    }

    @Override
    public Type getType() {
        return This.TYPE;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Node getChild(final int index) {
        throw new IndexOutOfBoundsException();
    }

    /**
     * Type implementation describing 'This' nodes.
     * @since 1.0.0
     */
    private static final class ThisType implements Type {
        @Override
        public String getName() {
            return This.NAME;
        }

        @Override
        public Builder createBuilder() {
            return new This.Constructor();
        }
    }

    /**
     * Constructor (builder) that creates nodes of the 'This' type.
     * @since 1.0.0
     */
    public static final class Constructor implements Builder {
    }
}
