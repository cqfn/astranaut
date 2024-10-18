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
package org.cqfn.astranaut.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.core.utils.FilesReader;

/**
 * Common methods for tests that perform end-to-end testing of the entire application
 * by running it with some parameters and comparing the result.
 * @since 1.0.0
 */
class EndToEndTest {
    /**
     * Loads a resource (e.g., reference code to compare with generated code) from a file.
     * @param name Name of the resource
     * @return Resource as a string
     */
    String loadStringResource(final String name) {
        final Path path = Paths.get("src/test/resources/end_to_end", name);
        return new FilesReader(path.toFile().getAbsolutePath()).readAsStringNoExcept();
    }

    /**
     * Returns the contents of all files within a folder (including subfolders)
     *  as a single listing.
     * @param root Path to root folder containing files and subfolders
     * @return Contents of all files as a single string
     */
    String getAllFilesContent(final Path root) {
        String listing = "";
        final List<Path> paths = new LinkedList<>();
        try {
            Files.walk(root).filter(Files::isRegularFile).forEach(paths::add);
            Collections.sort(paths, Comparator.comparing(Path::toString));
            final StringBuilder builder = new StringBuilder();
            for (final Path file : paths) {
                builder
                    .append(
                        new FilesReader(file.toFile().getAbsolutePath()).readAsStringNoExcept()
                    )
                    .append('\n');
            }
            listing = builder.toString();
        } catch (final IOException ignored) {
        }
        return listing;
    }
}
