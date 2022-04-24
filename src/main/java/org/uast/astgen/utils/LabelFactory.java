/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils;

import java.util.Arrays;
import java.util.List;

/**
 * The label factory for label generation.
 * The label cannot be an empty string and can be used as a variable name.
 *
 * @since 1.0
 */
public class LabelFactory {
    /**
     * The list of labels.
     */
    private static final List<String> LIST =
        Arrays.asList(
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
            "fourteenth",
            "fifteenth",
            "sixteenth",
            "seventeenth",
            "eighteenth",
            "nineteenth",
            "twentieth"
        );

    /**
     * The current index.
     */
    private int index;

    /**
     * Constructor.
     */
    public LabelFactory() {
        this.index = 0;
    }

    /**
     * Returns next label.
     * @return A label
     */
    public String getLabel() {
        if (this.index == LabelFactory.LIST.size()) {
            throw new IllegalStateException();
        }
        final String result = LabelFactory.LIST.get(this.index);
        this.index = this.index + 1;
        return result;
    }
}
