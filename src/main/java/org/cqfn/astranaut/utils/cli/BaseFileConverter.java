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
package org.cqfn.astranaut.utils.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Base implementation of CLI file parameter converter.
 *
 * @since 1.0
 */
public abstract class BaseFileConverter implements IStringConverter<File> {
    /**
     * The option name.
     */
    private final String option;

    /**
     * Constructor.
     * @param option An option name
     */
    public BaseFileConverter(final String option) {
        this.option = option;
    }

    @Override
    public final File convert(final String value) {
        if (!this.isValidFileExtension(value)) {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("The parameter [")
                .append(value)
                .append("] should be a");
            boolean flag = false;
            for (final String ext : this.getValidExtensions()) {
                if (flag) {
                    message.append(" or");
                }
                flag = true;
                message.append(" .").append(ext);
            }
            message.append(" file");
            throw new ParameterException(message.toString());
        }
        final File file = new File(value);
        if (this.fileMustExist() && !file.exists()) {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("The parameter for option [")
                .append(this.option)
                .append("] is not a valid file: ")
                .append(value);
            throw new ParameterException(message.toString());
        }
        return new File(value);
    }

    /**
     * Returns list of valid file extensions.
     * @return The list of file extensions
     */
    public abstract List<String> getValidExtensions();

    /**
     * Returns flag indicating that the file must exist.
     * @return The flag
     */
    public abstract boolean fileMustExist();

    /**
     * Checks if an input file extension is txt.
     *
     * @param value A path to file
     * @return A boolean {@code true} if a file has a valid extension or
     *  {@code false} otherwise
     */
    private boolean isValidFileExtension(final String value) {
        final Optional<String> ext = Optional.ofNullable(value)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(value.lastIndexOf('.') + 1));
        boolean valid = false;
        if (ext.isPresent()) {
            valid = this.getValidExtensions().contains(ext.get());
        }
        return valid;
    }
}
