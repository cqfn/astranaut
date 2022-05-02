/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.base.DraftNode;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorFactory;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.StringData;
import org.uast.astgen.utils.LabelFactory;

/**
 * Test for the {@link Matcher} class.
 *
 * @since 1.0
 */
public class MatcherTest {
    /**
     * Testing the simple case, when the descriptor contains a type only.
     */
    @Test
    public void testSimpleCase() {
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
    public void testDataMatching() {
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
     * Testing the case when the descriptor contains a data represented as a hole.
     */
    @Test
    public void testDataExtracting() {
        final String type = "identifier";
        final String data = "x";
        final DraftNode.Constructor ctor = new DraftNode.Constructor();
        ctor.setName(type);
        ctor.setData(data);
        final Node node = ctor.createNode();
        final LabelFactory labels = new LabelFactory();
        final DescriptorFactory factory = new DescriptorFactory(labels.getLabel(), type);
        factory.setData(new Hole(0));
        final Descriptor descriptor = factory.createDescriptor();
        final Matcher matcher = new Matcher(descriptor);
        final Map<Integer, String> collection = new TreeMap<>();
        final boolean result = matcher.match(node, Collections.emptyMap(), collection);
        Assertions.assertTrue(result);
        Assertions.assertTrue(collection.containsKey(0));
        Assertions.assertEquals(data, collection.get(0));
    }
}
