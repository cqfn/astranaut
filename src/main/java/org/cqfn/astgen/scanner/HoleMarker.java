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

package org.cqfn.astgen.scanner;

import org.cqfn.astgen.rules.Hole;
import org.cqfn.astgen.rules.HoleAttribute;

/**
 * The hole marker (# and a number after).
 *
 * @since 1.0
 */
public class HoleMarker implements Token {
    /**
     * Value of the hole.
     */
    private final int value;

    /**
     * Attribute.
     */
    private final HoleAttribute attribute;

    /**
     * Constructor.
     * @param value Value of the hole
     * @param attribute Attribute
     */
    public HoleMarker(final int value, final HoleAttribute attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder()
            .append('#')
            .append(this.value);
        if (this.attribute == HoleAttribute.ELLIPSIS) {
            builder.append("...");
        }
        return builder.toString();
    }

    /**
     * Creates a hole from this marker.
     * @return A hole
     */
    public Hole createHole() {
        return new Hole(this.value, this.attribute);
    }
}
