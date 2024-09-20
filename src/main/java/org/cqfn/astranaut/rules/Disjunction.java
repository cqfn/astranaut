/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import java.util.Collections;
import java.util.List;

/**
 * Disjunction, i.e. logical OR expression.
 *
 * @since 0.1.5
 */
public class Disjunction implements Child {
    /**
     * List of descriptors.
     */
    private final List<Descriptor> descriptors;

    /**
     * Constructor.
     *
     * @param descriptors List of descriptors
     */
    public Disjunction(final List<Descriptor> descriptors) {
        this.descriptors = Collections.unmodifiableList(descriptors);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        boolean flag = false;
        for (final Descriptor descriptor : this.descriptors) {
            if (flag) {
                builder.append(" | ");
            }
            builder.append(descriptor.toString());
            flag = true;
        }
        return builder.toString();
    }

    /**
     * Returns the list of descriptors.
     * @return List of descriptors
     */
    public List<Descriptor> getDescriptors() {
        return this.descriptors;
    }
}
