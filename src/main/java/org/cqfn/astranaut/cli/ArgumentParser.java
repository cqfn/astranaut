/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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
package org.cqfn.astranaut.cli;

import java.util.Iterator;
import java.util.List;

/**
 * Parses command line arguments.
 * @since 1.0.0
 */
public class ArgumentParser {
    /**
     * The folder or file where the result is placed.
     * And yes, I hate jcommander.
     */
    private String output;

    /**
     * Constructor.
     */
    public ArgumentParser() {
        this.output = "output";
    }

    /**
     * Parses command line parameters.
     * @param args List of parameters
     * @throws CliException If parsing failed
     */
    public void parse(final List<String> args) throws CliException {
        final Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            final String arg = iterator.next();
            if (arg.equals("--output") || arg.equals("-o")) {
                this.output = ArgumentParser.parseString(arg, iterator);
            }
        }
    }

    /**
     * Returns the name of the folder or file where the result is placed.
     * @return The folder/file name
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Parses the parameter value as a string.
     * @param name Name of the parameter
     * @param iterator Iterator by parameters
     * @return Parameter value as a string
     * @throws CliException If parsing failed
     */
    private static String parseString(final String name, final Iterator<String> iterator)
        throws CliException {
        boolean oops = true;
        String value = "";
        do {
            if (!iterator.hasNext()) {
                break;
            }
            value = iterator.next();
            if (value.charAt(0) == '-') {
                break;
            }
            oops = false;
        } while (false);
        if (oops) {
            throw new CommonCliException(
                String.format(
                    "The value after the '%s' parameter is expected",
                    name
                )
            );
        }
        return value;
    }
}
