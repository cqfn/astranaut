/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.rules;

import java.util.Collections;
import java.util.List;

/**
 * States that the descriptor is empty.
 *
 * @since 1.0
 */
public final class Empty extends Descriptor {
    /**
     * The instance.
     */
    public static final Empty INSTANCE = new Empty();

    /**
     * Constructor.
     */
    private Empty() {
    }

    @Override
    public DescriptorAttribute getAttribute() {
        return DescriptorAttribute.NONE;
    }

    @Override
    public String getTag() {
        return "";
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public List<Parameter> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public Data getData() {
        return InvalidData.INSTANCE;
    }
}
