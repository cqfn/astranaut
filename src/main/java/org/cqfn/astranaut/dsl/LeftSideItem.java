/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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
package org.cqfn.astranaut.dsl;

import java.util.Map;
import java.util.function.Function;
import org.cqfn.astranaut.codegen.java.Klass;
import org.cqfn.astranaut.codegen.java.LeftSideItemGenerator;
import org.cqfn.astranaut.codegen.java.NumberedLabelGenerator;

/**
 * A pattern descriptor or typed hole. It is located on the left side of the transformation rules.
 * @since 1.0.0
 */
public interface LeftSideItem {
    /**
     * Sets the matching mode.
     * @param mode Matching mode
     */
    void setMatchingMode(PatternMatchingMode mode);

    /**
     * Returns current matching mode.
     * @return Matching mode
     */
    PatternMatchingMode getMatchingMode();

    /**
     * Creates a generator that generates a class that implements the left side
     *  of the transformation rule.
     * @return A generator for creating corresponding class
     */
    LeftSideItemGenerator createGenerator();

    /**
     * Generates or retrieves a matcher class for this left-side item.
     *  If a matcher for this item already exists in the provided map, it is returned.
     *  Otherwise, a new matcher is generated and stored in the map.
     * @param matchers A map storing previously generated matcher classes
     * @param labels A generator for sequential labels used in class naming
     * @return A {@link Klass} instance representing a left-side implementation
     */
    default Klass generateMatcher(final Map<String, Klass> matchers,
        final NumberedLabelGenerator labels) {
        return matchers.computeIfAbsent(
            this.toString(),
            s -> LeftSideItem.this.createGenerator().generate(labels)
        );
    }
}
