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
package org.cqfn.astranaut.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests covering {@link CommentsRemover} class and tokens.
 * @since 1.0.0
 */
class CommentsRemoverTest {
    @Test
    void singleLineComment() {
        final String before = "This <- 0; // 'self' in Python\nExpression <- This | Literal;";
        final String expected = "This <- 0;                    \nExpression <- This | Literal;";
        final CommentsRemover remover = new CommentsRemover(before);
        final String after = remover.getUncommentedCode();
        Assertions.assertEquals(expected, after);
    }

    @Test
    void multiLineComment() {
        final String before = "/#\n   Expressions:\n#/\nThis <- 0;\nExpression <- This | Literal;"
            .replace('#', '*');
        final String expected =
            "  \n               \n  \nThis <- 0;\nExpression <- This | Literal;";
        final CommentsRemover remover = new CommentsRemover(before);
        final String after = remover.getUncommentedCode();
        Assertions.assertEquals(expected, after);
    }

    @Test
    void noComments() {
        final String before = "This <- 0;\nExpression <- This | Literal;";
        final CommentsRemover remover = new CommentsRemover(before);
        final String after = remover.getUncommentedCode();
        Assertions.assertEquals(before, after);
    }
}
