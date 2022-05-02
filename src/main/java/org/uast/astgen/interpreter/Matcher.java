/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.interpreter;

import java.util.List;
import java.util.Map;
import org.uast.astgen.base.Node;
import org.uast.astgen.rules.Data;
import org.uast.astgen.rules.Descriptor;
import org.uast.astgen.rules.Hole;
import org.uast.astgen.rules.Parameter;
import org.uast.astgen.rules.StringData;

/**
 * Matcher that works with the raw descriptor.
 *
 * @since 1.0
 */
public final class Matcher implements org.uast.astgen.base.Matcher {
    /**
     * The descriptor.
     */
    private final Descriptor descriptor;

    /**
     * Constructor.
     * @param descriptor The descriptor
     */
    public Matcher(final Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public boolean match(final Node node, final Map<Integer, Node> children,
        final Map<Integer, String> data) {
        return this.checkType(node) && this.checkChildCount(node)
            && this.checkAndExtractData(node, data)
            && this.checkAndExtractChildren(node, children, data);
    }

    /**
     * Checks the type matches.
     * @param node The node
     * @return Checking result, {@code true} if the type matches
     */
    private boolean checkType(final Node node) {
        return node.getType().getName().equals(this.descriptor.getType());
    }

    /**
     * Checks the number of child nodes matches.
     * @param node The node
     * @return Checking result, {@code true} if the number of child nodes matches
     */
    private boolean checkChildCount(final Node node) {
        return node.getChildCount() == this.descriptor.getParameters().size();
    }

    /**
     * Checks the data matches, extracts the data.
     * @param node The node
     * @param extracted The collection for saving extracted data
     * @return Checking result, {@code true} if the data matches
     */
    private boolean checkAndExtractData(final Node node, final Map<Integer, String> extracted) {
        final Data data = this.descriptor.getData();
        final boolean result;
        if (data instanceof StringData) {
            result = node.getData().equals(((StringData) data).getValue());
        } else if (data instanceof Hole) {
            extracted.put(((Hole) data).getValue(), node.getData());
            result = true;
        } else {
            result = node.getData().isEmpty();
        }
        return result;
    }

    /**
     * Checks the child nodes matches, extracts the children.
     * @param node The node
     * @param children The collection for saving extracted children
     * @param data The collection for saving extracted data
     * @return Checking result, {@code true} if the data matches
     */
    private boolean checkAndExtractChildren(final Node node, final Map<Integer, Node> children,
        final Map<Integer, String> data) {
        boolean result = true;
        final List<Parameter> parameters = this.descriptor.getParameters();
        assert node.getChildCount() == parameters.size();
        int index = 0;
        for (final Parameter parameter : parameters) {
            final Node child = node.getChild(index);
            if (parameter instanceof Hole) {
                children.put(((Hole) parameter).getValue(), child);
            } else if (parameter instanceof Descriptor) {
                final Matcher mather = new Matcher((Descriptor) parameter);
                result = mather.match(child, children, data);
                if (!result) {
                    break;
                }
            }
            index = index + 1;
        }
        return result;
    }
}
