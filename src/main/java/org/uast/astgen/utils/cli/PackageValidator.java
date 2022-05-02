/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.utils.cli;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import java.util.regex.Pattern;

/**
 * Validator of a package CLI parameter.
 *
 * @since 1.0
 */
public class PackageValidator implements IParameterValidator {
    @Override
    /**
     * Validates an input option parameter that should contain
     * a package name.
     * @param name The option name
     * @param value The option value
     * @throws ParameterException
     */
    public void validate(final String name, final String value) throws ParameterException {
        if (value.charAt(0) == '-') {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("The parameter for the option [")
                .append(name)
                .append("] is missed");
            throw new ParameterException(message.toString());
        }
        final String pattern = "(([a-z])+\\.)+([a-z])+";
        final boolean matches = Pattern.matches(pattern, value);
        if (!matches) {
            final StringBuilder message = new StringBuilder(50);
            message
                .append("Input parameter: ")
                .append(value)
                .append(" for the option [")
                .append(name)
                .append("] should follow Java's package name rules");
            throw new ParameterException(message.toString());
        }
    }
}
