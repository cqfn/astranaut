/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.rules;

import org.uast.astgen.utils.StringUtils;

/**
 * Data represents as a string.
 *
 * @since 1.0
 */
public final class StringData implements Data {
    /**
     * The value.
     */
    private final String value;

    /**
     * Constructor.
     * @param value The value
     */
    public StringData(final String value) {
        this.value = value;
    }

    /**
     * Returns the value.
     * @return The value
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append('\"')
            .append(new StringUtils(this.value).escapeEntities())
            .append('\"')
            .toString();
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
