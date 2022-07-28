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
package org.cqfn.astranaut.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for reading files.
 *
 * @since 0.1.5
 */
public class FilesReader {
    /**
     * The file path.
     */
    private final Path path;

    /**
     * Constructor.
     * @param path Path as a string
     */
    public FilesReader(final String path) {
        this.path = Paths.get(path);
    }

    /**
     * Reads file content as string.
     * @return File content
     * @throws IOException If the file can't be read
     */
    public String readAsString() throws IOException {
        final InputStream stream = Files.newInputStream(this.path);
        final StringBuilder builder = new StringBuilder();
        for (int chr = stream.read(); chr != -1; chr = stream.read()) {
            if (chr != '\r') {
                builder.append((char) chr);
            }
        }
        stream.close();
        return builder.toString();
    }

    /**
     * Reads file content as string.
     * @param creator Exception creator
     * @param <T> Exception type
     * @return File content
     * @throws T If the file can't be read
     */
    public <T extends Exception> String readAsString(
        final CustomExceptionCreator<T> creator) throws T {
        try {
            return this.readAsString();
        } catch (final IOException ignored) {
            throw creator.create();
        }
    }

    /**
     * Custom exception creator.
     * @param <T> Exception type
     * @since 0.1.5
     */
    public interface CustomExceptionCreator<T extends Exception> {
        /**
         * Creates new exception object.
         * @return The nex exception object
         */
        T create();
    }
}
