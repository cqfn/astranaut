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
package org.cqfn.astranaut.codegen.java;

import java.util.Collections;
import java.util.Set;
import org.cqfn.astranaut.dsl.RegularNodeDescriptor;
import org.cqfn.astranaut.exceptions.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link RegularNodeGenerator} class.
 * @since 1.0.0
 */
class RegularNodeGeneratorTest extends CodegenTest {
    @Test
    void nodeWithoutChildren() {
        final RegularNodeDescriptor rule = new RegularNodeDescriptor(
            "This",
            Collections.emptyList()
        );
        final RegularNodeGenerator generator = new RegularNodeGenerator(rule);
        final Set<CompilationUnit> units = generator.createUnits(this.createContext());
        Assertions.assertEquals(1, units.size());
        boolean oops = false;
        try {
            final String code = units.iterator().next().generateJavaCode();
            Assertions.assertEquals(
                this.loadStringResource("regular_node_without_children.java"),
                code
            );
        } catch (final BaseException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
