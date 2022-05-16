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

package org.cqfn.astgen.analyzer;

import java.util.Objects;
import org.cqfn.astgen.codegen.java.TaggedChild;

/**
 * The class to store tagged names.
 *
 * @since 1.0
 */
public final class TaggedName implements TaggedChild {
    /**
     * The tag.
     */
    private final String tag;

    /**
     * The type.
     */
    private final String type;

    /**
     * The flag indicates that the tag is overridden.
     */
    private boolean overridden;

    /**
     * Constructor.
     * @param tag The tag
     * @param type The type
     */
    TaggedName(final String tag, final String type) {
        this.tag = tag;
        this.type = type;
        this.overridden = false;
    }

    /**
     * Makes the tagged name overridden.
     */
    public void makeOverridden() {
        this.overridden = true;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean isOverridden() {
        return this.overridden;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder
            .append('{')
            .append(this.tag)
            .append(", ")
            .append(this.type)
            .append(", ")
            .append(this.overridden)
            .append('}');
        return builder.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        final TaggedName name;
        boolean equal = false;
        if (obj instanceof TaggedName) {
            name = (TaggedName) obj;
            if (this.type.equals(name.getType())
                && this.tag.equals(name.getTag())) {
                equal = true;
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tag + this.type);
    }
}
