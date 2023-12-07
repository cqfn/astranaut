/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.codegen.java;

import org.cqfn.astranaut.utils.StringUtils;

/**
 * Java method body.
 *
 * @since 0.1.5
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
            String line = lines[index];
            int gap = 0;
            if (line.startsWith("\t")) {
                final int len = line.length();
                for (int symbol = 0; symbol < len; symbol = symbol + 1) {
                    if (line.charAt(symbol) == '\t') {
                        gap = gap + 1;
                    } else {
                        break;
                    }
                }
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '}') {
                offset = offset - 1;
            }
            builder.append(StringUtils.SPACE.repeat((indent + offset + gap) * Entity.TAB_SIZE))
                .append(line)
                .append('\n');
            if (line.endsWith("{")) {
                offset = offset + 1;
            }
        }
        return builder.toString();
    }
}
