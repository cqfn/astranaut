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
package org.cqfn.astranaut.utils;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link CommentsRemover} class.
 *
 * @since 0.2.2
 */
public class CommentsRemoverTest {
    /**
     * The folder with test resources.
     */
    private static final String TESTS_PATH = "src/test/resources/comments/";

    /**
     * Regular expression that matches spaces.
     */
    private static final String SPACES = "\\s+";

    /**
     * Test for removing of single-line comments from a DSL program.
     */
    @Test
    public void testSingleLineCommentsRemove() {
        final String expected = this.readTest("single_line_comments_expected.dsl");
        final String source = this.readTest("single_line_comments_source.dsl");
        final String actual = new CommentsRemover(source).remove();
        Assertions.assertEquals(
            expected.replaceAll(CommentsRemoverTest.SPACES, ""),
            actual.replaceAll(CommentsRemoverTest.SPACES, "")
        );
    }

    /**
     * Test for removing of multi-line comments from a DSL program.
     */
    @Test
    public void testMultiLineCommentsRemove() {
        final String expected = this.readTest("multi_line_comments_expected.dsl");
        final String source = this.readTest("multi_line_comments_source.dsl");
        final String actual = new CommentsRemover(source).remove();
        Assertions.assertEquals(
            expected.replaceAll(CommentsRemoverTest.SPACES, ""),
            actual.replaceAll(CommentsRemoverTest.SPACES, "")
        );
    }

    /**
     * Test for removing of comments from a DSL program that contains comment tokens in nodes data.
     */
    @Test
    public void testCommentsRemoveWithCommentTokensInData() {
        final String expected = this.readTest("tokens_in_data_expected.dsl");
        final String source = this.readTest("tokens_in_data_source.dsl");
        final String actual = new CommentsRemover(source).remove();
        Assertions.assertEquals(
            expected.replaceAll(CommentsRemoverTest.SPACES, ""),
            actual.replaceAll(CommentsRemoverTest.SPACES, "")
        );
    }

    /**
     * Reads test source from the file.
     * @param name The file name
     * @return Test source
     */
    private String readTest(final String name) {
        String result = "";
        boolean oops = false;
        try {
            result = new FilesReader(CommentsRemoverTest.TESTS_PATH.concat(name))
                .readAsString();
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        return result;
    }
}
