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
package org.cqfn.astranaut.utils;

import java.util.Arrays;
import java.util.List;

/**
 * The label factory for label generation.
 * The label cannot be an empty string and can be used as a variable name.
 *
 * @since 0.1.5
 */
public class LabelFactory {
    /**
     * The list of labels.
     */
    private static final List<String> LIST =
        Arrays.asList(
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth",
            "twentieth"
        );

    /**
     * The current index.
     */
    private int index;

    /**
     * First label.
     */
    private final String first;

    /**
     * Constructor.
     * @param first First label
     */
    public LabelFactory(final String first) {
        this.first = first;
        this.index = 0;
    }

    /**
     * Another constructor.
     */
    public LabelFactory() {
        this(null);
    }

    /**
     * Returns next label.
     * @return A label
     */
    public String getLabel() {
        if (this.index == LabelFactory.LIST.size()) {
            throw new IllegalStateException();
        }
        final String result;
        if (this.index > 0 || this.first == null) {
            result = LabelFactory.LIST.get(this.index);
        } else {
            result = this.first;
        }
        this.index = this.index + 1;
        return result;
    }
}
