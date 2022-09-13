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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cqfn.astranaut.core.EmptyTree;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.cqfn.astranaut.interpreter.Adapter;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.rules.Transformation;

/**
 * API for a tree processing.
 *
 * @since 0.2
 */
public class TreeProcessor {
    /**
     * Rules of a tree transformation.
     */
    private final List<Instruction<Transformation>> rules;

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

    /**
     * Counts an amount of transformation rules.
     * @return Rules amount
     */
    public int countRules() {
        return this.rules.size();
    }

    /**
     * Calculates the number of variants of a single rule
     * application.
     * @param index The rule index
     * @param tree The initial tree to be modified
     * @return Amount of application variants
     */
    public int calculateVariants(final int index, final Node tree) {
        int result;
        try {
            final Instruction<Transformation> rule = this.rules.get(index);
            final Adapter adapter = new Adapter(Collections.singletonList(rule));
            result =  adapter.calculateConversions(tree);
        } catch (final IndexOutOfBoundsException exception) {
            result = 0;
        }
        return result;
    }

    /**
     * Applies specified variant of transformation to the tree.
     * @param index The rule index
     * @param variant The variant index
     * @param tree The initial tree to be modified
     * @return Tree with chosen variant of transformation applied
     */
    public Node partialTransform(final int index, final int variant, final Node tree) {
        Node result;
        try {
            final Instruction<Transformation> rule = this.rules.get(index);
            final Adapter adapter = new Adapter(Collections.singletonList(rule));
            result =  adapter.partialConvert(variant, tree);
        } catch (final IndexOutOfBoundsException exception) {
            result = EmptyTree.INSTANCE;
        }
        return result;
    }
}
