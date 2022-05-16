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
package org.cqfn.astgen.base;

/**
 * This class describes child node within type descriptor.
 *
 * @since 1.0
 */
public final class ChildDescriptor {
    /**
     * The name of the child type.
     */
    private final String type;

    /**
     * Flag that states that the child is optional.
     */
    private final boolean optional;

    /**
     * Constructor.
     * @param type The name of the child type
     * @param optional Flag that states that the child is optional
     */
    public ChildDescriptor(final String type, final  boolean optional) {
        this.type = type;
        this.optional = optional;
    }

    /**
     * Additional constructor.
     * @param type The name of the child type
     */
    public ChildDescriptor(final String type) {
        this(type, false);
    }

    /**
     * Returns the name of the child type.
     * @return The name
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the flag that states that the child is optional.
     * @return The optional flag
     */
    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public String toString() {
        final String result;
        if (this.optional) {
            result = new StringBuilder()
                .append('[')
                .append(this.type)
                .append(']')
                .toString();
        } else {
            result = this.type;
        }
        return result;
    }
}
