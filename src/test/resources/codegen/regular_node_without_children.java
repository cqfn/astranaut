/*
 * Copyright (c) 2024 John Doe
 */
package org.cqfn.astranaut.test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<Node> getChildrenList() {
        return Collections.emptyList();
    }

    /**
     * Type implementation describing 'This' nodes.
     * @since 1.0.0
     */
    private static final class ThisType implements Type {
        /**
         * Node hierarchy.
         */
        private static final List<String> HIERARCHY = Collections.singletonList(This.NAME);

        @Override
        public String getName() {
            return This.NAME;
        }

        @Override
        public List<String> getHierarchy() {
            return ThisType.HIERARCHY;
        }

        @Override
        public Map<String, String> getProperties() {
            return CommonFactory.PROPERTIES;
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
        /**
         * Fragment of source code that is associated with the node.
         */
        private Fragment fragment;

        @Override
        public void setFragment(final Fragment object) {
            this.fragment = object;
        }

        @Override
        public boolean setData(final String value) {
            return value.isEmpty();
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            return list.isEmpty();
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Node createNode() {
            final This node = new This();
            node.fragment = this.fragment;
            return node;
        }
    }
}
