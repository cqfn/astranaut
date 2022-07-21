/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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
package org.cqfn.astranaut.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Draft node for wrapping the results of third-party parsers
 * in the {@link Node} interface.
 *
 * @since 0.1.5
 */
public final class DraftNode implements Node {
    /**
     * The fragment associated with the node.
     */
    private Fragment fragment;

    /**
     * The node type.
     */
    private Type type;

    /**
     * The node data.
     */
    private String data;

    /**
     * The list of children nodes.
     */
    private List<Node> children;

    /**
     * Private constructor.
     */
    private DraftNode() {
    }

    @Override
    public Fragment getFragment() {
        return this.fragment;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public int getChildCount() {
        return this.children.size();
    }

    @Override
    public Node getChild(final int index) {
        return this.children.get(index);
    }

    /**
     * Type implementation for the draft node.
     *
     * @since 0.1.5
     */
    private static class TypeImpl implements Type {
        /**
         * The type name.
         */
        private final String name;

        /**
         * Constructor.
         * @param name The type name
         */
        TypeImpl(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public List<ChildDescriptor> getChildTypes() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getHierarchy() {
            return Collections.singletonList(this.name);
        }

        @Override
        public String getProperty(final String property) {
            return "";
        }

        @Override
        public Builder createBuilder() {
            return new Constructor();
        }
    }

    /**
     * The constructor class for draft node.
     * @since 0.1.5
     */
    public static final class Constructor implements Builder {
        /**
         * The fragment associated with the node.
         */
        private Fragment fragment;

        /**
         * The type name.
         */
        private String name;

        /**
         * The node data.
         */
        private String data;

        /**
         * The list of children nodes.
         */
        private final List<Node> children;

        /**
         * Constructor.
         */
        public Constructor() {
            this.fragment = EmptyFragment.INSTANCE;
            this.name = "";
            this.data = "";
            this.children = new LinkedList<>();
        }

        @Override
        public void setFragment(final Fragment obj) {
            this.fragment = obj;
        }

        /**
         * Sets the type name.
         * @param str Type mane
         */
        public void setName(final String str) {
            this.name = str;
        }

        @Override
        public boolean setData(final String str) {
            this.data = Objects.requireNonNull(str);
            return true;
        }

        /**
         * Adds a child node.
         * @param node Node
         */
        public void addChild(final Node node) {
            this.children.add(Objects.requireNonNull(node));
        }

        @Override
        public boolean setChildrenList(final List<Node> list) {
            this.children.clear();
            this.children.addAll(list);
            return true;
        }

        @Override
        public boolean isValid() {
            return !this.name.isEmpty();
        }

        @Override
        public DraftNode createNode() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final DraftNode node = new DraftNode();
            node.fragment = this.fragment;
            node.type = new TypeImpl(this.name);
            node.data = this.data;
            node.children = new ArrayList<>(this.children);
            return node;
        }
    }
}
