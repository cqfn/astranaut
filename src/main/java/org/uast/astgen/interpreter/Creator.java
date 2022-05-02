/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.uast.astgen.base.Builder;
import org.uast.astgen.base.EmptyTree;
import org.uast.astgen.base.Factory;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Data;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.rules.StringData;

/**
 * Creates a node as described in the descriptor.
 *
 * @since 1.0
 */
public class Creator {
    /**
     * The descriptor.
     */
    private final Descriptor descriptor;

    /**
     * The list of nested creators.
     */
    private final Creator[] subs;

    /**
     * Constructor.
     * @param descriptor The descriptor
     */
    public Creator(final Descriptor descriptor) {
        this.descriptor = descriptor;
        this.subs = new Creator[descriptor.getParameters().size()];
    }

    /**
     * Creates a node as described in the descriptor.
     * @param factory The node factory
     * @param children The collection contains extracted children
     * @param data The collection contains extracted data
     * @return A node
     */
    public Node create(final Factory factory, final Map<Integer, Node> children,
        final Map<Integer, String> data) {
        final Builder builder = factory.createBuilder(this.descriptor.getType());
        final List<Node> list = new LinkedList<>();
        int index = 0;
        for (final Parameter parameter : this.descriptor.getParameters()) {
            if (parameter instanceof Hole) {
                final Node child = children.getOrDefault(
                    ((Hole) parameter).getValue(),
                    EmptyTree.INSTANCE
                );
                list.add(child);
            } else if (parameter instanceof Descriptor) {
                final Creator creator;
                if (this.subs[index] == null) {
                    creator = new Creator((Descriptor) parameter);
                    this.subs[index] = creator;
                } else {
                    creator = this.subs[index];
                }
                final Node child = creator.create(factory, children, data);
                list.add(child);
            }
            index = index + 1;
        }
        builder.setChildrenList(list);
        this.setData(builder, data);
        return builder.createNode();
    }

    /**
     * Sets data to the node builder.
     * @param builder The node builder
     * @param extracted Data, extracted from source syntax tree
     */
    private void setData(final Builder builder, final Map<Integer, String> extracted) {
        final Data data = this.descriptor.getData();
        if (data instanceof Hole) {
            final int index = ((Hole) data).getValue();
            if (extracted.containsKey(index)) {
                builder.setData(extracted.get(index));
            }
        } else if (data instanceof StringData) {
            builder.setData(((StringData) data).getValue());
        }
    }
}
