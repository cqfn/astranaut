/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.util.Objects;
import org.cqfn.astranaut.utils.StringUtils;

/**
 * Hole, i.e. #1, #2, Modifier#3, etc.
 *
 * @since 0.1.5
 */
public final class Hole implements Data, Parameter {
    /**
     * Value of the hole.
     */
    private final int value;

    /**
     * Attribute.
     */
    private final HoleAttribute attribute;

    /**
     * Type of the node that is matched by a hole.
     */
    private final String type;

    /**
     * Constructor.
     * @param value Value of the hole
     * @param attribute Attribute
     * @param type Type of the node
     */
    public Hole(final int value, final HoleAttribute attribute, final String type) {
        this.value = value;
        this.attribute = attribute;
        this.type = type;
    }

    /**
     * Returns the value of the hole.
     * @return The value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the type of the hole.
     * @return The type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the attribute of the hole.
     * @return The attribute
     */
    public HoleAttribute getAttribute() {
        return this.attribute;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
            .append(this.type)
            .append('#')
            .append(this.value);
        if (this.attribute == HoleAttribute.ELLIPSIS) {
            builder.append("...");
        }
        return builder.toString();
    }

    @Override
    public void toStringIndented(StringBuilder builder, int indent) {
        builder.append(StringUtils.SPACE.repeat(indent))
            .append(this.type)
            .append('#')
            .append(this.value);
        if (this.attribute == HoleAttribute.ELLIPSIS) {
            builder.append("...");
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        final Hole hole;
        boolean equal = false;
        if (obj instanceof Hole) {
            hole = (Hole) obj;
            if (this.value == hole.getValue()) {
                equal = true;
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }
}
