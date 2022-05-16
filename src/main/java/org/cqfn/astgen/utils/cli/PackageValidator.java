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
package org.cqfn.astgen.utils.cli;

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
