/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
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

import java.io.IOException;
import org.cqfn.astranaut.utils.FilesReader;

/**
 * The header that contains license.
 *
 * @since 1.0
 */
public final class License {
    /**
     * The file name.
     */
    private final String file;

    /**
     * Flag indicating that the object is in an invalid state.
     */
    private boolean invalid;

    /**
     * The file content.
     */
    private String data;

    /**
     * Constructor.
     * @param file The name of the file that contains license
     */
    public License(final String file) {
        this.file = file;
        this.invalid = false;
        this.data = "";
    }

    /**
     * Checks the license file is valid.
     * @return Checking result
     */
    public boolean isValid() {
        this.init();
        return !this.invalid;
    }

    /**
     * Generates source code.
     * @return Source code
     */
    public String generate() {
        this.init();
        return this.data;
    }

    /**
     * Reads a file and prepares data.
     */
    private void init() {
        if (!this.invalid && this.data.isEmpty()) {
            final FilesReader reader = new FilesReader(this.file);
            try {
                final String content = reader.readAsString().trim();
                if (content.isEmpty()) {
                    this.invalid = true;
                } else {
                    this.prepare(content);
                }
            } catch (final IOException ignored) {
                this.invalid = true;
            }
        }
    }

    /**
     * Prepares the data.
     * @param content The file content
     */
    private void prepare(final String content) {
        final String[] lines = content.split("\n");
        final StringBuilder builder = new StringBuilder();
        builder.append("/*\n");
        for (final String line : lines) {
            if (line.isEmpty()) {
                builder.append(" *\n");
            } else {
                builder.append(" * ").append(line).append('\n');
            }
        }
        builder.append(" */\n\n");
        this.data = builder.toString();
    }
}
