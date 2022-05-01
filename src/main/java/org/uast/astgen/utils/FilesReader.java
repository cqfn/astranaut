/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for reading files.
 *
 * @since 1.0
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
     * @since 1.0
     */
    public interface CustomExceptionCreator<T extends Exception> {
        /**
         * Creates new exception object.
         * @return The nex exception object
         */
        T create();
    }
}
