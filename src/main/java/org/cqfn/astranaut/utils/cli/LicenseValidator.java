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

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import java.io.File;

/**
 * Validator of a project root CLI parameter.
 *
 * @since 0.1.5
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
