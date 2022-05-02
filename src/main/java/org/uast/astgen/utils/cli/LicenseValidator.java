/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils.cli;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import java.io.File;

/**
 * Validator of a project root CLI parameter.
 *
 * @since 1.0
 */
public final class LicenseValidator implements IParameterValidator {
    @Override
    /**
     * Validates an input option parameter that should contain
     * a project root path.
     * @param name The option name
     * @param value The option value
     * @throws ParameterException
     */
    public void validate(final String name, final String value) throws ParameterException {
        if (value.charAt(0) == '-') {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("Missed parameter for the option [")
                .append(name)
                .append(']');
            throw new ParameterException(message.toString());
        }
        final File file = new File(value);
        if (!file.exists()) {
            throw new ParameterException(String.format("License not exists: %s", value));
        }
    }
}
