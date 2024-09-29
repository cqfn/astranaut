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
package org.cqfn.astranaut.dsl;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link RegularNodeDescriptor} class.
 * @since 1.0.0
 */
class RegularNodeDescriptorTest {
    @Test
    void nodeWithoutChildren() {
        final RegularNodeDescriptor rule = new RegularNodeDescriptor(
            "This",
            Collections.emptyList()
        );
        final String actual = rule.toString();
        final String expected = "This <- 0";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithVarietyOfChildren() {
        final RegularNodeDescriptor rule = new RegularNodeDescriptor(
            "Variable",
            Arrays.asList(
                new ChildDescriptorExt(true, "", "Type"),
                new ChildDescriptorExt(false, "name", "Identifier"),
                new ChildDescriptorExt(true, "init", "Expression")
            )
        );
        final String actual = rule.toString();
        final String expected = "Variable <- [Type], name@Identifier, [init@Expression]";
        Assertions.assertEquals(expected, actual);
    }
}
