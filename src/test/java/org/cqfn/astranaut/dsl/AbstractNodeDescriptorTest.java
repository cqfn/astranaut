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
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link AbstractNodeDescriptor} class.
 * @since 1.0.0
 */
class AbstractNodeDescriptorTest {
    @Test
    void nodeWithoutSubtypes() {
        boolean oops = false;
        try {
            new AbstractNodeDescriptor("Invalid", Collections.emptyList());
        } catch (final IllegalArgumentException ignored) {
            oops = true;
        }
        Assertions.assertTrue(oops);
    }

    @Test
    void nodeWithOneSubtype() {
        final AbstractNodeDescriptor rule = new AbstractNodeDescriptor(
            "Statement",
            Collections.singletonList("Synchronized")
        );
        final String actual = rule.toString();
        final String expected = "Statement <- Synchronized | ?";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithMultipleSubtypes() {
        final AbstractNodeDescriptor rule = new AbstractNodeDescriptor(
            "BinaryOperation",
            Arrays.asList("Addition", "Subtraction", "Multiplication")
        );
        final String actual = rule.toString();
        final String expected = "BinaryOperation <- Addition | Subtraction | Multiplication";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nodeWithRepeatedSubtypes() {
        final AbstractNodeDescriptor rule = new AbstractNodeDescriptor(
            "LogicalOperation",
            Arrays.asList("And", "Or", "Xor", "Or")
        );
        final String actual = rule.toString();
        final String expected = "LogicalOperation <- And | Or | Xor";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void mergeTags() {
        final String exprtype = "Expression";
        final AbstractNodeDescriptor abstrakt = new AbstractNodeDescriptor(
            exprtype,
            Arrays.asList("FieldAccess", "BinaryOperation")
        );
        Assertions.assertTrue(abstrakt.getTags().isEmpty());
        final LiteralDescriptor.Constructor ctor =
            new LiteralDescriptor.Constructor("Identifier");
        ctor.setType("String");
        ctor.setInitial("\"\"");
        final NodeDescriptor identifier = ctor.createDescriptor();
        final String lefttag = "left";
        ChildDescriptorExt left = new ChildDescriptorExt(
            false,
            lefttag,
            exprtype
        );
        left.setRule(abstrakt);
        final String righttag = "right";
        ChildDescriptorExt right = new ChildDescriptorExt(
            false,
            righttag,
            "Identifier"
        );
        right.setRule(identifier);
        final RegularNodeDescriptor access = new RegularNodeDescriptor(
            "FieldAccess",
            Arrays.asList(left, right)
        );
        abstrakt.mergeTags(access.getTags());
        Map<String, ChildDescriptorExt> tags = abstrakt.getTags();
        Assertions.assertTrue(tags.containsKey(lefttag));
        Assertions.assertTrue(tags.containsKey(righttag));
        left = new ChildDescriptorExt(
            false,
            lefttag,
            exprtype
        );
        left.setRule(abstrakt);
        right = new ChildDescriptorExt(
            false,
            righttag,
            exprtype
        );
        right.setRule(abstrakt);
        final RegularNodeDescriptor binary = new RegularNodeDescriptor(
            "BinaryOperation",
            Arrays.asList(left, right)
        );
        abstrakt.mergeTags(binary.getTags());
        tags = abstrakt.getTags();
        Assertions.assertTrue(tags.containsKey(lefttag));
        Assertions.assertFalse(tags.containsKey(righttag));
    }
}
