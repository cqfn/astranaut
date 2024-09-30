/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Entity that represents documentation for another Java entity.
 * @since 1.0.0
 */
public final class JavaDoc implements Entity {
    /**
     * Brief description of the entity.
     */
    private final String brief;

    /**
     * Constructor.
     * @param brief Brief description of the entity
     */
    public JavaDoc(final String brief) {
        this.brief = brief
            .replaceAll("[\\n\\t]+", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        code.add(indent, "/*");
        final String[] words;
        if (this.brief.endsWith(".")) {
            words = this.brief.split(" ");
        } else {
            words = this.brief.concat(".").split(" ");
        }
        final int length = SourceCodeBuilder.MAX_LINE_LENGTH
            - SourceCodeBuilder.TABULATION.length() * indent - 4;
        final List<String> lines = new LinkedList<>();
        StringBuilder line = new StringBuilder();
        for (final String word : words) {
            if (line.length() + word.length() + 1 <= length) {
                if (line.length() > 0) {
                    line.append(' ');
                }
                line.append(word);
            } else {
                lines.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        lines.add(line.toString());
        final Iterator<String> iterator = lines.iterator();
        code.add(indent, String.format(" * %s", iterator.next()));
        while (iterator.hasNext()) {
            code.add(indent, String.format(" *  %s", iterator.next()));
        }
        code.add(indent, " */");
    }
}
