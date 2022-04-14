/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.io.IOException;
import org.uast.astgen.utils.FilesReader;

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
            builder.append(" * ").append(line).append('\n');
        }
        builder.append(" */\n\n");
        this.data = builder.toString();
    }
}
