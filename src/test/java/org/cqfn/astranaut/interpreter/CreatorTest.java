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
package org.cqfn.astranaut.interpreter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.cqfn.astranaut.core.base.DefaultFactory;
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.DummyNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.exceptions.ParserException;
import org.cqfn.astranaut.parser.BracketsParser;
import org.cqfn.astranaut.parser.DescriptorParser;
import org.cqfn.astranaut.parser.Tokenizer;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorAttribute;
import org.cqfn.astranaut.scanner.TokenList;
import org.cqfn.astranaut.utils.LabelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that covers {@link Creator} class.
 *
 * @since 0.2.6
 */
class CreatorTest {
    /**
     * Tests creation of a node from the hole that was found in the left part of the rule.
     */
    @Test
    void testCreationFromHole() {
        final Descriptor descriptor = this.parseCode("#1");
        final Creator creator = new Creator(descriptor);
        final Map<Integer, List<Node>> children = new TreeMap<>();
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName("A");
        final Node node = ctor.createNode();
        children.put(1, Collections.singletonList(node));
        final Node result = creator.create(
            DefaultFactory.EMPTY,
            children,
            Collections.emptyMap()
        );
        Assertions.assertEquals(node, result);
    }

    /**
     * Tests creation of a node from the hole that was not found in the left part of the rule.
     */
    @Test
    void testCreationFromNotFoundHole() {
        final Descriptor descriptor = this.parseCode("#2");
        final Creator creator = new Creator(descriptor);
        final Node result = creator.create(
            DefaultFactory.EMPTY, Collections.emptyMap(), Collections.emptyMap()
        );
        Assertions.assertEquals(DummyNode.INSTANCE, result);
    }

    /**
     * Tests reuse of creators for nested descriptors.
     */
    @Test
    void testReuseOfCreators() {
        final Descriptor descriptor = this.parseCode("X(Y,Z)");
        final Creator creator = new Creator(descriptor);
        final List<Creator> initial = creator.getSubs();
        creator.create(DefaultFactory.EMPTY, Collections.emptyMap(), Collections.emptyMap());
        final List<Creator> first = creator.getSubs();
        creator.create(DefaultFactory.EMPTY, Collections.emptyMap(), Collections.emptyMap());
        final List<Creator> second = creator.getSubs();
        Assertions.assertNotEquals(initial, first);
        Assertions.assertEquals(first, second);
    }

    /**
     * Tests adding data that was extracted from the left part of the rule
     * to the node builder.
     */
    @Test
    void testSettingExtractedDataToBuilder() {
        final Descriptor descriptor = this.parseCode("B<#1>");
        final Creator creator = new Creator(descriptor);
        final Map<Integer, String> data = new TreeMap<>();
        final String value = "test";
        data.put(1, value);
        final Node result = creator.create(DefaultFactory.EMPTY, Collections.emptyMap(), data);
        Assertions.assertEquals("B", result.getTypeName());
        Assertions.assertEquals(value, result.getData());
    }

    /**
     * Parses a descriptor from source code.
     * @param code DSL code
     * @return A descriptor
     */
    private Descriptor parseCode(final String code) {
        Descriptor result = null;
        boolean oops = false;
        try {
            TokenList tokens = new Tokenizer(code).getTokens();
            tokens = new BracketsParser(tokens).parse();
            result = new DescriptorParser(tokens, new LabelFactory())
                .parse(DescriptorAttribute.NONE);
        } catch (final ParserException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
