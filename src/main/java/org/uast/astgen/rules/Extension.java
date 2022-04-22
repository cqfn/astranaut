/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.Collections;
import java.util.List;

/**
 * States that the ancestor node extends the green abstract node
 * with the same name.
 *
 * @since 1.0
 */
public final class Extension extends Descriptor {
    /**
     * The instance.
     */
    public static final Extension INSTANCE = new Extension();

    /**
     * Constructor.
     */
    private Extension() {
    }

    @Override
    public DescriptorAttribute getAttribute() {
        return DescriptorAttribute.EXT;
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
