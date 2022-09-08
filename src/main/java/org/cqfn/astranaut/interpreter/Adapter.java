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
package org.cqfn.astranaut.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.rules.Instruction;
import org.cqfn.astranaut.rules.Transformation;

/**
 * Tree converter built on a set of rules described in DSL.
 *
 * @since 0.1.5
 */
public class Adapter extends org.cqfn.astranaut.core.Adapter {
    /**
     * Constructor.
     * @param instructions The list of transformation instructions
     */
    public Adapter(final List<Instruction<Transformation>> instructions) {
        super(Collections.unmodifiableList(Adapter.init(instructions)), Factory.INSTANCE);
    }

    /**
     * Initialises the list of converters.
     * @param instructions The list of transformation instructions
     * @return List of converters
     */
    private static List<org.cqfn.astranaut.core.Converter> init(
        final List<Instruction<Transformation>> instructions) {
        final List<org.cqfn.astranaut.core.Converter> result =
            new ArrayList<>(instructions.size());
        for (final Instruction<Transformation> instruction : instructions) {
            final Transformation rule = instruction.getRule();
            result.add(new Converter(rule));
        }
        return result;
    }
}
