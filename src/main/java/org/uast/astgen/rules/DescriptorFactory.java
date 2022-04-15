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
     * The label.
     */
    private final String label;

    /**
     * The type.
     */
    private String type;

    /**
     * The list of parameters.
     */
    private final List<Parameter> parameters;

    /**
     * The data.
     */
    private Data data;

    /**
     * Constructor.
     * @param label The label
     * @param type The name
     */
    public DescriptorFactory(final String label, final String type) {
        this.attribute = DescriptorAttribute.NONE;
        this.tag = "";
        this.label = label;
        this.type = Objects.requireNonNull(type);
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
     * Sets the new type.
     * @param value The new name
     */
    public void replaceType(final String value) {
        if (this.tag.isEmpty()) {
            this.tag = this.type;
        }
        this.type = value;
    }

    /**
     * Adds the parameter to the parameters list.
     * @param parameter Parameter
     */
    public void addParameter(final Parameter parameter) {
        this.parameters.add(parameter);
    }

    /**
     * Replaces all parameters.
     * @param list The new list of parameters
     */
    public void setParameters(final List<Parameter> list) {
        this.parameters.clear();
        this.parameters.addAll(list);
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
        public String getLabel() {
            return this.factory.label;
        }

        @Override
        public String getType() {
            return this.factory.type;
        }

        @Override
        public List<Parameter> getParameters() {
            return Collections.unmodifiableList(this.factory.parameters);
        }

        @Override
        public Data getData() {
            return this.factory.data;
        }
    }
}
