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
import org.cqfn.astranaut.core.base.DraftNode;
import org.cqfn.astranaut.core.base.Node;
import org.cqfn.astranaut.rules.Descriptor;
import org.cqfn.astranaut.rules.DescriptorFactory;
import org.cqfn.astranaut.rules.Hole;
import org.cqfn.astranaut.rules.HoleAttribute;
import org.cqfn.astranaut.rules.StringData;
import org.cqfn.astranaut.utils.LabelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for the {@link Matcher} class.
 *
 * @since 0.1.5
 */
class MatcherTest {
    /**
     * Testing the simple case, when the descriptor contains a type only.
     */
    @Test
    void testSimpleCase() {
        final String type = "PublicModifier";
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        final DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), type);
        final Descriptor descriptor = factory.createDescriptor();
        final Matcher matcher = new Matcher(descriptor);
        final boolean result = matcher.match(node, Collections.emptyMap(), Collections.emptyMap());
        Assertions.assertTrue(result);
    }

    /**
     * Testing the case when the descriptor contains a data represented as a string.
     */
    @Test
    void testDataMatching() {
        final String type = "literal";
        final String data = "+";
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setData(data);
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        final DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), type);
        factory.setData(new StringData(data));
        final Descriptor descriptor = factory.createDescriptor();
        final Matcher matcher = new Matcher(descriptor);
        final boolean result = matcher.match(node, Collections.emptyMap(), Collections.emptyMap());
        Assertions.assertTrue(result);
    }

    /**
     * Testing the case when the descriptor contains a data
     * that does not match with the node data.
     */
    @Test
    void testDataMismatching() {
        final String type = "integer";
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setData("2");
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        final DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), type);
        factory.setData(new StringData("3"));
        final Descriptor descriptor = factory.createDescriptor();
        final Matcher matcher = new Matcher(descriptor);
        final boolean result = matcher.match(node, Collections.emptyMap(), Collections.emptyMap());
        Assertions.assertFalse(result);
    }

    /**
     * Testing the case when the descriptor contains a data represented as a hole.
     */
    @Test
    void testDataExtracting() {
        final String type = "identifier";
        final String data = "x";
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setData(data);
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        final DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), type);
        factory.setData(new Hole(0, HoleAttribute.NONE, ""));
        final Descriptor descriptor = factory.createDescriptor();
        final Matcher matcher = new Matcher(descriptor);
        final Map<Integer, String> collection = new TreeMap<>();
        final boolean result = matcher.match(node, Collections.emptyMap(), collection);
        Assertions.assertTrue(result);
        Assertions.assertTrue(collection.containsKey(0));
        Assertions.assertEquals(data, collection.get(0));
    }

    /**
     * Testing the case when the descriptor contains another descriptor that contains
     * a data represented as a string.
     */
    @Test
    void testChildrenMatching() {
        final String type = "simpleIdentifier";
        final String subtype = "number";
        final String data = "13";
        DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(subtype);
        ctor.setData(data);
        final Node nested = ctor.createNode();
        ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setChildrenList(Collections.singletonList(nested));
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), subtype);
        factory.setData(new StringData(data));
        final Descriptor subdescr = factory.createDescriptor();
        factory = new DescriptorFactory(labels.getLabel(), type);
        factory.addParameter(subdescr);
        final Matcher matcher = new Matcher(factory.createDescriptor());
        final boolean result = matcher.match(node, Collections.emptyMap(), Collections.emptyMap());
        Assertions.assertTrue(result);
    }

    /**
     * Testing the case when the descriptor contains children represented as a hole.
     */
    @Test
    void testChildrenExtracting() {
        final String type = "return";
        final String subtype = "stringLiteral";
        final String data = "xxx";
        DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(subtype);
        ctor.setData(data);
        final Node nested = ctor.createNode();
        ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setChildrenList(Collections.singletonList(nested));
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        final DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), type);
        factory.addParameter(new Hole(1, HoleAttribute.NONE, ""));
        final Map<Integer, List<Node>> extracted = new TreeMap<>();
        final boolean result = new Matcher(factory.createDescriptor())
            .match(node, extracted, Collections.emptyMap());
        Assertions.assertTrue(result);
        Assertions.assertTrue(extracted.containsKey(1));
        Assertions.assertEquals(data, extracted.get(1).get(0).getData());
    }
}
