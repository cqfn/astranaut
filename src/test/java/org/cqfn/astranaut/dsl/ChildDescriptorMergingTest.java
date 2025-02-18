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

import java.util.Arrays;
import java.util.Collections;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link ChildDescriptorExt#merge(ChildDescriptorExt)} method.
 * @since 1.0.0
 */
class ChildDescriptorMergingTest {
    @Test
    void mismatchedTags() {
        final ChildDescriptorExt first = new ChildDescriptorExt(
            false,
            "first",
            "Type"
        );
        final ChildDescriptorExt second = new ChildDescriptorExt(
            false,
            "second",
            "AnotherType"
        );
        final ChildDescriptorExt merged = first.merge(second);
        Assertions.assertNull(merged);
    }

    @Test
    void noCommonParents() {
        final ChildDescriptorExt first = new ChildDescriptorExt(
            false,
            "value",
            "Expression"
        );
        first.setRule(
            new AbstractNodeDescriptor(
                "Expressions",
                Collections.singletonList("Expression")
            )
        );
        final ChildDescriptorExt second = new ChildDescriptorExt(
            false,
            "value",
            "Identifier"
        );
        second.setRule(
            new AbstractNodeDescriptor(
                "Identifiers",
                Collections.singletonList("Identifier")
            )
        );
        final ChildDescriptorExt merged = first.merge(second);
        Assertions.assertNull(merged);
    }

    @Test
    void firstOptionalAndSecondMandatory() {
        final boolean result = this.oneOptionalAndOneMandatory(true, false);
        Assertions.assertTrue(result);
    }

    @Test
    void firstMandatoryAndSecondOptional() {
        final boolean result = this.oneOptionalAndOneMandatory(false, true);
        Assertions.assertTrue(result);
    }

    /**
     * Tests merging two descriptors with different optional flags.
     * @param first Optional flag of the first descriptor
     * @param second Optional flag of the second descriptor
     * @return Optional flag of the resulting descriptor
     */
    private boolean oneOptionalAndOneMandatory(final boolean first, final boolean second) {
        boolean flag = false;
        boolean oops = false;
        try {
            final ChildDescriptorExt alpha = new ChildDescriptorExt(
                first,
                "tag",
                "FirstType"
            );
            final AbstractNodeDescriptor parent = new AbstractNodeDescriptor(
                "Parent",
                Arrays.asList("FirstType", "SecondType")
            );
            RegularNodeDescriptor descriptor = new RegularNodeDescriptor(
                "FirstType",
                Collections.emptyList()
            );
            descriptor.addBaseDescriptor(parent);
            alpha.setRule(descriptor);
            final ChildDescriptorExt beta = new ChildDescriptorExt(
                second,
                "tag",
                "SecondType"
            );
            descriptor = new RegularNodeDescriptor(
                "SecondType",
                Collections.emptyList()
            );
            descriptor.addBaseDescriptor(parent);
            beta.setRule(descriptor);
            final ChildDescriptorExt merged = alpha.merge(beta);
            Assertions.assertEquals("Parent", merged.getType());
            flag = merged.isOptional();
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return flag;
    }
}
