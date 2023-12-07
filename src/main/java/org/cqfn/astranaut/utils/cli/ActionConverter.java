/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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
import org.cqfn.astranaut.Action;

/**
 * Custom implementation of CLI file parameter converter.
 *
 * @since 0.1.5
 */
public class ActionConverter implements IStringConverter<Action> {
    /**
     * The option name.
     */
    private final String option;

    /**
     * Constructor.
     * @param option An option name
     */
    public ActionConverter(final String option) {
        this.option = option;
    }

    /**
     * Converts a command-line parameter to an action.
     *
     * @param value An action as a string
     * @return An action as an object
     */
    public Action convert(final String value) {
        final Action result;
        switch (value) {
            case "generate":
                result = Action.GENERATE;
                break;
            case "convert":
                result = Action.CONVERT;
                break;
            default:
                throw new ParameterException(
                    String.format(
                        "The parameter for the option [%s] is not a valid action",
                        this.option
                    )
                );
        }
        return result;
    }
}
