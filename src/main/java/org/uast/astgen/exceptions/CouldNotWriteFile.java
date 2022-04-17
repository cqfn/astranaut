/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.exceptions;

/**
 * Exception "Could not write file".
 *
 * @since 1.0
 */
public final class CouldNotWriteFile extends GeneratorException {
    /**
     * The file name.
     */
    private final String filename;

    /**
     * Constructor.
     * @param filename The file name
     */
    public CouldNotWriteFile(final String filename) {
        this.filename = filename;
    }

    @Override
    public String getErrorMessage() {
        return String.format("Could not write file: '%s'", this.filename);
    }
}
