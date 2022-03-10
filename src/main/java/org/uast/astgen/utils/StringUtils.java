/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */

package org.uast.astgen.utils;

/**
 * Some string utilities.
 *
 * @since 1.0
 */
public class StringUtils {
    /**
     * Value to be processed.
     */
    private final String value;

    /**
     * Constructor.
     * @param value Value to be processed.
     */
    public StringUtils(final String value) {
        this.value = value;
    }

    /**
     * Replaces entities (brackets, line breaks) in the string.
     * @return Result
     */
    public String escapeEntities() {
        final StringBuilder builder = new StringBuilder();
        final int length = this.value.length();
        for (int index = 0; index < length; index = index + 1) {
            final char symbol = this.value.charAt(index);
            switch (symbol) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\"':
                    builder.append("\\\"");
                    break;
                case '\'':
                    builder.append("\\'");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(symbol);
                    break;
            }
        }
        return builder.toString();
    }
}
