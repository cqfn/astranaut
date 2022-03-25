/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The factory for descriptors composing.
 *
 * @since 1.0
 */
public class DescriptorFactory {
    /**
     * The attribute.
     */
    private DescriptorAttribute attribute;

    /**
     * The tag.
     */
    private String tag;

    /**
     * The name.
     */
    private String name;

    /**
     * The list of parameters.
     */
    private final List<Descriptor> parameters;

    /**
     * The data.
     */
    private Data data;

    /**
     * Constructor.
     * @param name The name
     */
    public DescriptorFactory(final String name) {
        this.attribute = DescriptorAttribute.NONE;
        this.tag = "";
        this.name = Objects.requireNonNull(name);
        this.parameters = new LinkedList<>();
        this.data = InvalidData.INSTANCE;
    }

    /**
     * Sets the new attribute.
     * @param value Attribute
     */
    public void setAttribute(final DescriptorAttribute value) {
        this.attribute = value;
    }

    /**
     * Sets the new name.
     * @param value The new name
     */
    public void replaceName(final String value) {
        if (this.tag.isEmpty()) {
            this.tag = this.name;
        }
        this.name = value;
    }

    /**
     * Adds the parameter to the parameters list.
     * @param parameter Parameter
     */
    public void addParameter(final Descriptor parameter) {
        this.parameters.add(parameter);
    }

    /**
     * Sets the new data.
     * @param value Data
     */
    public void setData(final Data value) {
        this.data = value;
    }

    /**
     * Creates a descriptor from collected parameters.
     * @return New descriptor
     */
    public Descriptor createDescriptor() {
        return new DescriptorObject(this);
    }

    /**
     * Implementation of the {@link Descriptor} class.
     *
     * @since 1.0
     */
    private static class DescriptorObject extends Descriptor {
        /**
         * The factory.
         */
        private final DescriptorFactory factory;

        /**
         * Constructor.
         * @param factory The factory
         */
        DescriptorObject(final DescriptorFactory factory) {
            this.factory = factory;
        }

        @Override
        public DescriptorAttribute getAttribute() {
            return this.factory.attribute;
        }

        @Override
        public String getTag() {
            return this.factory.tag;
        }

        @Override
        public String getName() {
            return this.factory.name;
        }

        @Override
        public List<Descriptor> getParameters() {
            return Collections.unmodifiableList(this.factory.parameters);
        }

        @Override
        public Data getData() {
            return this.factory.data;
        }
    }
}
