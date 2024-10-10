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

import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Entity representing the text of some license added to the beginning of each generated file.
 * @since 1.0.0
 */
public final class License implements Entity {
    /**
     * Text of the license, broken down line by line.
     */
    private final String[] lines;

    /**
     * Constructor.
     * @param text Text of the license
     */
    public License(final String text) {
        this.lines = License.prepareText(text);
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        code.add(indent, "/*");
        for (int index = 0; index < this.lines.length; index = index + 1) {
            if (this.lines[index].isEmpty()) {
                code.add(indent, " *");
            } else {
                code.add(indent, String.format(" * %s", this.lines[index]));
            }
        }
        code.add(indent, " */");
    }

    /**
     * Prepares the license text by breaking it line by line and removing spaces
     *  at the beginning and end of each line.
     * @param text Text of the license
     * @return Text of the license, broken down line by line
     */
    private static String[] prepareText(final String text) {
        final String[] array = text.trim().replace("\r", "").split("\n");
        for (int index = 0; index < array.length; index = index + 1) {
            array[index] = array[index].trim();
        }
        return array;
    }
}
