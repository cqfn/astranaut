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
import java.util.List;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link NodeDescriptor} class.
 * @since 1.0.0
 */
class NodeDescriptorTest {
    @Test
    void nodeWithCycleInheritance() {
        boolean oops = false;
        final AbstractNodeDescriptor first = new AbstractNodeDescriptor(
            "A",
            Collections.singletonList("B")
        );
        final AbstractNodeDescriptor second = new AbstractNodeDescriptor(
            "B",
            Collections.singletonList("C")
        );
        final AbstractNodeDescriptor third = new AbstractNodeDescriptor(
            "C",
            Collections.singletonList("A")
        );
        try {
            second.addBaseDescriptor(first);
            third.addBaseDescriptor(second);
            first.addBaseDescriptor(third);
        } catch (final BaseException exception) {
            oops = true;
            Assertions.assertEquals("Analyzer", exception.getInitiator());
            Assertions.assertEquals(
                "Adding this descriptor would create a cycle: 'C <- A | ?'",
                exception.getErrorMessage()
            );
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void nodeWithMultipleInheritance() {
        boolean oops = false;
        final AbstractNodeDescriptor first = new AbstractNodeDescriptor(
            "A",
            Arrays.asList("B", "C")
        );
        final AbstractNodeDescriptor second = new AbstractNodeDescriptor(
            "B",
            Collections.singletonList("D")
        );
        final AbstractNodeDescriptor third = new AbstractNodeDescriptor(
            "C",
            Collections.singletonList("E")
        );
        final AbstractNodeDescriptor fourth = new AbstractNodeDescriptor(
            "D",
            Collections.singletonList("E")
        );
        final RegularNodeDescriptor fifth = new RegularNodeDescriptor(
            "E",
            Collections.emptyList()
        );
        try {
            second.addBaseDescriptor(first);
            third.addBaseDescriptor(first);
            fourth.addBaseDescriptor(second);
            fifth.addBaseDescriptor(fourth);
            fifth.addBaseDescriptor(third);
        } catch (final BaseException exception) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final List<String> hierarchy = fifth.getHierarchy();
        final String expected = "E, D, B, C, A";
        final String actual = String.join(", ", hierarchy);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void language() {
        final RegularNodeDescriptor descriptor = new RegularNodeDescriptor(
            "Null",
            Collections.emptyList()
        );
        Assertions.assertEquals("", descriptor.getLanguage());
        descriptor.setLanguage("Java");
        Assertions.assertEquals("java", descriptor.getLanguage());
    }
}
