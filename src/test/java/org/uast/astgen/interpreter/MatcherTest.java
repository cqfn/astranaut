/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uast.astgen.base.DraftNode;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.DescriptorFactory;
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
}
