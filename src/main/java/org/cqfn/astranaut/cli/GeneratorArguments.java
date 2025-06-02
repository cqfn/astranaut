/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
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

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import org.cqfn.astranaut.core.utils.FilesReader;

/**
 * Parses command line arguments for generator.
 * @since 1.0.0
 */
public final class GeneratorArguments extends BaseArguments {
    /**
     * The folder or file where the result is placed.
     * And yes, I hate jcommander.
     */
    private String output;

    /**
     * License text.
     */
    private String licence;

    /**
     * Package name.
     */
    private String pkg;

    /**
     * Version number.
     */
    private String version;

    /**
     * Constructor.
     */
    public GeneratorArguments() {
        this.output = "output";
        this.licence = String.format(
            "Copyright (c) %d %s",
            LocalDate.now().getYear(),
            System.getProperty("user.name")
        );
        this.pkg = "ast";
        this.version = "1.0.0";
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
            switch (arg) {
                case "--output":
                case "-o":
                    this.setOutput(this.parseString(arg, iterator));
                    break;
                case "--license":
                case "-l":
                    this.parseLicense(arg, iterator);
                    break;
                case "--package":
                case "-p":
                    this.parsePackageName(arg, iterator);
                    break;
                case "--version":
                case "-v":
                    this.parseVersion(arg, iterator);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sets the name of the folder or file where the result is placed.
     * @param value Name of destination folder
     */
    public void setOutput(final String value) {
        this.output = value;
    }

    /**
     * Returns the name of the folder or file where the result is placed.
     * @return The folder/file name
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Sets the file containing license. The license will be read from this file.
     * @param name File name
     * @throws CliException If reading failed
     */
    public void setLicenseFile(final String name) throws CliException {
        this.licence = new FilesReader(name)
            .readAsString(
                new FilesReader.CustomExceptionCreator<CliException>() {
                    @Override
                    public CliException create() {
                        return new CommonCliException(
                            String.format(
                                "Can't read the license file '%s'",
                                name
                            )
                        );
                    }
                }
            );
    }

    /**
     * Returns the text of the license to be added to the generated files.
     * @return License text.
     */
    public String getLicence() {
        return this.licence;
    }

    /**
     * Sets the package name and checks it for correctness.
     * @param name Package name
     * @throws CliException If checking failed
     */
    public void setPackage(final String name) throws CliException {
        final String pattern = "^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$";
        if (!name.matches(pattern)) {
            throw new CommonCliException(
                String.format(
                    "The string '%s' is not a valid Java package name",
                    name
                )
            );
        }
        this.pkg = name;
    }

    /**
     * Returns the name of the packet. This name will be used in the generated files.
     * @return Package name
     */
    public String getPackage() {
        return this.pkg;
    }

    /**
     * Sets the version number and checks it for correctness.
     * @param value Version number
     * @throws CliException If checking failed
     */
    public void setVersion(final String value) throws CliException {
        final String pattern = "^\\d+(\\.\\d+){1,2}(\\.[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?$";
        if (!value.matches(pattern)) {
            throw new CommonCliException(
                String.format(
                    "The string '%s' is not a valid version number",
                    value
                )
            );
        }
        this.version = value;
    }

    /**
     * Returns the version number of the generated files.
     *  This number will be added to the description of each generated class and interface.
     * @return Version number
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Parses the name of the file containing the license and reads the file.
     * @param arg Parameter
     * @param iterator Iterator by parameters
     * @throws CliException If parsing or reading failed
     */
    private void parseLicense(final String arg, final Iterator<String> iterator)
        throws CliException {
        final String name = this.parseString(arg, iterator);
        this.setLicenseFile(name);
    }

    /**
     * Parses the package name and checks it for correctness.
     * @param arg Parameter
     * @param iterator Iterator by parameters
     * @throws CliException If parsing or checking failed
     */
    private void parsePackageName(final String arg, final Iterator<String> iterator)
        throws CliException {
        final String name = this.parseString(arg, iterator);
        this.setPackage(name);
    }

    /**
     * Parses the version number and checks it for correctness.
     * @param arg Parameter
     * @param iterator Iterator by parameters
     * @throws CliException If parsing or checking failed
     */
    private void parseVersion(final String arg, final Iterator<String> iterator)
        throws CliException {
        final String value = this.parseString(arg, iterator);
        this.setVersion(value);
    }
}
