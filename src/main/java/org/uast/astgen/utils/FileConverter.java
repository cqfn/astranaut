/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.Optional;

/**
 * Custom implementation of CLI file parameter converter.
 *
 * @since 1.0
 */
public class FileConverter implements IStringConverter<File> {
    /**
     * The option name.
     */
    private final String option;

    /**
     * Constructor.
     * @param option An option name
     */
    public FileConverter(final String option) {
        this.option = option;
    }

    /**
     * Converts a command-line parameter to a file.
     *
     * @param value A path to file
     * @return A new file
     */
    public File convert(final String value) {
        if (!isValidFileExtension(value)) {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("The parameter [")
                .append(value)
                .append("] should be a txt file, found: ");
            throw new ParameterException(message.toString());
        }
        final File file = new File(value);
        if (!file.exists()) {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("The parameter for option [")
                .append(this.option)
                .append("] is not a valid file:")
                .append(value);
            throw new ParameterException(message.toString());
        }
        return new File(value);
    }

    /**
     * Checks if an input file extension is txt.
     *
     * @param value A path to file
     * @return A boolean {@code true} if a file has a valid extension or
     *  {@code false} otherwise
     */
    private static boolean isValidFileExtension(final String value) {
        final Optional<String> ext = Optional.ofNullable(value)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(value.lastIndexOf('.') + 1));
        boolean valid = false;
        if (ext.isPresent()) {
            valid = "txt".equals(ext.get());
        }
        return valid;
    }
}
