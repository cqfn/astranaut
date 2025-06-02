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
import org.cqfn.astranaut.codegen.java.Klass;
import org.cqfn.astranaut.codegen.java.LeftSideGenerationContext;
import org.cqfn.astranaut.codegen.java.LeftSideItemGenerator;
import org.cqfn.astranaut.core.algorithms.conversion.Extracted;
import org.cqfn.astranaut.core.base.Node;

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
     * Sets a flag that says the pattern is only applied if it does NOT match.
     */
    void setNegationFlag();

    /**
     * Returns a flag that indicates that the pattern is only applied if it does NOT match.
     * @return Negaton flag
     */
    boolean isNegationFlagSet();

    /**
     * Creates a generator that generates a class that implements the left side
     *  of the transformation rule.
     * @return A generator for creating corresponding class
     */
    LeftSideItemGenerator createGenerator();

    /**
     * Represents the item as a string.
     * @param full Full format, the matching mode is considered
     * @return Left side item represented as a string
     */
    String toString(boolean full);

    /**
     * Ensures that a matcher class for this left-side item exists in the provided map.
     *  If a matcher for this item already exists in the map, the method does nothing.
     *  Otherwise, a new matcher is generated and stored in the map.
     * @param context Generation context
     * @return The matcher class for this left-side item
     */
    default Klass generateMatcher(final LeftSideGenerationContext context) {
        final String key = this.toString(false);
        final Klass klass;
        final Map<String, Klass> matchers = context.getMatchers();
        if (matchers.containsKey(key)) {
            klass = matchers.get(key);
        } else {
            klass = this.createGenerator().generate(context);
            matchers.put(key, klass);
        }
        return klass;
    }

    /**
     * For the interpreter: matches a node with this descriptor and extracts child nodes
     *  and/or data when matched.
     * @param node Node to be matched
     * @param extracted Extracted nodes and data
     * @return Matching result, {@code true} if matched
     */
    boolean matchNode(Node node, Extracted extracted);
}
