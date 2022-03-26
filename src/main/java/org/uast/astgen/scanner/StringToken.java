/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.scanner;

import org.uast.astgen.rules.Data;
import org.uast.astgen.rules.StringData;
import org.uast.astgen.utils.StringUtils;

/**
 * Token that represents string.
 *
 * @since 1.0
 */
public final class StringToken implements Token {
    /**
     * The data.
     */
    private final String data;

    /**
     * Constructor.
     *
     * @param data The data
     */
    public StringToken(final String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append('\"')
            .append(new StringUtils(this.data).escapeEntities())
            .append('\"')
            .toString();
    }

    /**
     * Created a {@link Data} instance from this token.
     * @return Data instance
     */
    public StringData createStringData() {
        return new StringData(this.data);
    }
}
