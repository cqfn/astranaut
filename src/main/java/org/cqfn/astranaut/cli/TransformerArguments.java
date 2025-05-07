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

import java.util.Iterator;
import java.util.List;

/**
 * Parses command line arguments for transformer.
 * @since 1.0.0
 */
public final class TransformerArguments extends Arguments {
    /**
     * The name of the file for processing.
     */
    private String source;

    /**
     * Name of the programming language for which the transformation is performed
     *  (if the rule set has a division into programming languages).
     */
    private String language;

    /**
     * The name of the file into which the resulting tree is saved.
     */
    private String tree;

    /**
     * The name of the file into which the visualized resulting tree is saved.
     */
    private String image;

    /**
     * Constructor.
     */
    public TransformerArguments() {
        this.source = "";
        this.language = "common";
        this.tree = "";
        this.image = "";
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
                case "--source":
                case "-s":
                    this.source = this.parseString(arg, iterator);
                    break;
                case "--language":
                case "-l":
                    this.language = this.parseString(arg, iterator);
                    break;
                case "--ast":
                case "-t":
                    this.tree = this.parseString(arg, iterator);
                    break;
                case "--image":
                case "-i":
                    this.image = this.parseString(arg, iterator);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Returns the name of the file for processing.
     * @return Source text for processing
     */
    public String getSourceFilePath() {
        return this.source;
    }

    /**
     * Returns the name of the programming language for which the transformation is performed.
     * @return Language name
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Returns the name of the file into which the resulting tree is saved.
     * @return File name
     */
    public String getResultingTreePath() {
        return this.tree;
    }

    /**
     * Returns the name of the file into which the visualized resulting tree is saved.
     * @return File name
     */
    public String getResultingImagePath() {
        return this.image;
    }
}
