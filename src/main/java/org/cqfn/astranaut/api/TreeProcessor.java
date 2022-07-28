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
package org.cqfn.astranaut.api;

import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.base.Node;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.cqfn.astranaut.interpreter.Adapter;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.rules.Statement;
import org.cqfn.astranaut.rules.Transformation;
import org.cqfn.astranaut.utils.FilesReader;

/**
 * API for a tree processing.
 *
 * @since 0.2
 */
public class TreeProcessor {
    /**
     * Rules of a tree transformation.
     */
    private final List<Statement<Transformation>> rules;

    /**
     * Constructor.
     */
    public TreeProcessor() {
        this.rules = new LinkedList<>();
    }

    /**
     * Loads rules of a tree transformation from a DSL file.
     * @param filename The name of a file that contains DSL rules
     * @return The result, {@code true} if rules were successfully loaded
     * @throws ProcessorException If a file processing fails
     */
    public boolean loadRules(final String filename) throws ProcessorException {
        final String code = new FilesReader(filename).readAsString(
            (FilesReader.CustomExceptionCreator<ProcessorException>)
                () -> new ProcessorException() {
                    private static final long serialVersionUID = 6082572706459180749L;
                    @Override
                    public String getErrorMessage() {
                        return String.format("Could not read DSL file: %s", filename);
                    }
                }
        );
        return this.loadRulesFromString(code);
    }

    /**
     * Loads rules of a tree transformation from the given string.
     * @param code DSL rules of a transformation
     * @return The result, {@code true} if rules were successfully loaded
     */
    public boolean loadRulesFromString(final String code) {
        final ProgramParser parser = new ProgramParser(code);
        boolean success = true;
        try {
            final Program program = parser.parse();
            this.rules.addAll(program.getTransformations());
        } catch (final BaseException ignored) {
            success = false;
        }
        return success;
    }

    /**
     * Transforms an initial tree with the given rules.
     * @param tree The initial tree to be modified
     * @return Transformed tree
     */
    public Node transform(final Node tree) {
        final Adapter adapter = new Adapter(this.rules);
        return adapter.convert(tree);
    }
}
