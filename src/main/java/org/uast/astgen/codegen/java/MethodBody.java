/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.utils.StringUtils;

/**
 * Java method body.
 *
 * @since 1.0
 */
public final class MethodBody implements Entity {
    /**
     * The code.
     */
    private String code;

    /**
     * Constructor.
      */
    public MethodBody() {
        this.code = "";
    }

    /**
     * Sets the new code.
     * @param str The new code
     */
    public void setCode(final String str) {
        this.code = str;
    }

    @Override
    public String generate(final int indent) {
        final StringBuilder builder = new StringBuilder();
        final String[] lines = this.code.replace("{", "{\n")
            .replace("}", "\n}")
            .split("\n");
        int offset = 0;
        for (int index = 0; index < lines.length; index = index + 1) {
            final String line = lines[index].trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '}') {
                offset = offset - 1;
            }
            builder.append(StringUtils.SPACE.repeat((indent + offset) * Entity.TAB_SIZE))
                .append(line)
                .append('\n');
            if (line.endsWith("{")) {
                offset = offset + 1;
            }
        }
        return builder.toString();
    }
}
