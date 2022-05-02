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
public final class ProjectRootValidator implements IParameterValidator {
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
        boolean created = false;
        try {
            created = file.mkdirs();
            if (!file.exists()) {
                final StringBuilder message = new StringBuilder(70);
                message
                    .append("The parameter for the option [")
                    .append(name)
                    .append("] should be a root path, found: ")
                    .append(value);
                throw new ParameterException(message.toString());
            }
        } finally {
            if (created) {
                file.delete();
            }
        }
    }
}
