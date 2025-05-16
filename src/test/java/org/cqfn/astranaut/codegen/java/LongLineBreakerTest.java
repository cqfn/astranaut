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
package org.cqfn.astranaut.codegen.java;

import java.util.List;
import org.cqfn.astranaut.core.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link LongLineBreaker} class.
 * @since 1.0.0
 */
class LongLineBreakerTest {
    @Test
    void tooLongSectionInCallChain() {
        final String code =
            "longLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongMethodName().doSomethingElse();";
        final LongLineBreaker llb = new LongLineBreaker(code, 0, 0);
        final List<Pair<String, Integer>> result = llb.split();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void tooLongLastSectionInCallChain() {
        final String code =
            "methodName().doSomethingAndSomethingAndSomethingAndSomethingAndSomethingAndSomethingAndSomethingAndSomethingAndSomethingElse();";
        final LongLineBreaker llb = new LongLineBreaker(code, 0, 0);
        final List<Pair<String, Integer>> result = llb.split();
        Assertions.assertTrue(result.isEmpty());
    }
}
