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
package org.cqfn.astranaut.rules;

import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Descriptor, i.e. name with tag, parameters and data.
 * Example: tag@Name(parameter)
 *
 * @since 0.1.5
 */
public abstract class Descriptor implements Child, Parameter {
    /**
     * Returns the attribute of descriptor.
     * @return The attribute
     */
    public abstract DescriptorAttribute getAttribute();

    /**
     * Returns tag associated with descriptor.
     * @return The tag (or empty string).
     */
    public abstract String getTag();

    /**
     * Returns the label associated with descriptor.
     * The label cannot be an empty string and can be used as a variable name.
     * @return The label
     */
    public abstract String getLabel();

    /**
     * Returns type associated with descriptor.
     * @return The name (can't be {@code null} or empty)
     */
    public abstract String getType();

    /**
     * Returns list of parameters inside descriptor.
     * @return List of parameters
     */
    public abstract List<Parameter> getParameters();

    /**
     * Returns data associated with descriptor.
     * @return Data
     */
    public abstract Data getData();

    /**
     * Returns the hole number if the descriptor decorates a hole.
     * @return Hole number
     */
    public abstract int getHoleNumber();

    /**
     * Returns the name of the variable to which the node corresponding
     * to this descriptor can be written.
     * @return Variable name
     */
    public String getVariableName() {
        String result = this.getTag();
        if (result.isEmpty()) {
            result = this.getLabel();
        }
        return result.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Returns tag associated with descriptor, started from capital letter.
     * @return The tag
     */
    public String getTagCapital() {
        final String tag = this.getTag();
        return new StringBuilder().append(tag.substring(0, 1).toUpperCase(Locale.ENGLISH))
            .append(tag.substring(1)).toString();
    }

    /**
     * Returns the full name (i.e. tag and name).
     * @return The name (can't be {@code null} or empty)
     */
    public String getFullName() {
        final String tag = this.getTag();
        final String name = this.getType();
        final String result;
        if (tag.isEmpty()) {
            result = name;
        } else {
            result = new StringBuilder()
                .append(tag)
                .append('@')
                .append(name)
                .toString();
        }
        return result;
    }

    /**
     * Checks whether the descriptor has a hole.
     * @return Checking result, {@code true} if the descriptor has at least one hole
     */
    public boolean hasHole() {
        boolean result = false;
        if (this.getData() instanceof Hole) {
            result = true;
        }
        if (!result) {
            result = this.hasHoleWithAttribute(attribute -> true);
        }
        return result;
    }

    /**
     * Checks whether the descriptor has a typed hole.
     * @return Checking result, {@code true} if the descriptor has a typed hole
     */
    public boolean hasTypedHole() {
        return this.hasHoleWithAttribute(attribute -> attribute == HoleAttribute.TYPED);
    }

    /**
     * Checks whether the descriptor has a hole with ellipsis or a node type.
     * @return Checking result, {@code true} if the descriptor has a hole with ellipsis
     */
    public boolean hasEllipsisOrTypedHole() {
        return this.hasHoleWithAttribute(
            attribute -> attribute == HoleAttribute.ELLIPSIS || attribute == HoleAttribute.TYPED
        );
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        final DescriptorAttribute attribute = this.getAttribute();
        if (attribute == DescriptorAttribute.OPTIONAL) {
            builder.append('[');
        } else if (attribute == DescriptorAttribute.LIST) {
            builder.append('{');
        }
        final String tag = this.getTag();
        if (!tag.isEmpty()) {
            builder.append(tag).append('@');
        }
        builder.append(this.getType());
        this.parametersToString(builder);
        final Data data = this.getData();
        if (data.isValid()) {
            builder.append('<').append(data.toString()).append('>');
        }
        if (attribute == DescriptorAttribute.OPTIONAL) {
            builder.append(']');
        } else if (attribute == DescriptorAttribute.LIST) {
            builder.append('}');
        }
        if (attribute == DescriptorAttribute.EXT) {
            builder.append('&');
        }
        if (attribute == DescriptorAttribute.HOLE) {
            builder.append('#').append(this.getHoleNumber());
        }
        return builder.toString();
    }

    /**
     * Builds parameters list as a string.
     * @param builder String builder where to build.
     */
    private void parametersToString(final StringBuilder builder) {
        final List<Parameter> parameters = this.getParameters();
        if (!parameters.isEmpty()) {
            boolean flag = false;
            builder.append('(');
            for (final Parameter parameter : parameters) {
                if (flag) {
                    builder.append(", ");
                }
                flag = true;
                builder.append(parameter.toString());
            }
            builder.append(')');
        }
    }

    /**
     * Checks whether the descriptor has a hole with specified attribute.
     * @param checker Checker that checks the attribute matches some criteria
     * @return Checking result, {@code true} if the descriptor has a hole with specified attribute
     */
    private boolean hasHoleWithAttribute(final AttributeChecker checker) {
        boolean result = false;
        final List<Parameter> parameters = this.getParameters();
        final ListIterator<Parameter> iterator = parameters.listIterator(parameters.size());
        while (iterator.hasPrevious()) {
            final Parameter parameter = iterator.previous();
            if (parameter instanceof Hole && checker.check(((Hole) parameter).getAttribute())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checker for hole attribute.
     *
     * @since 0.2.8
     */
    private interface AttributeChecker {
        /**
         * Checks the attribute matches some criteria.
         * @param attribute Attribute
         * @return Checking result
         */
        boolean check(HoleAttribute attribute);
    }
}
