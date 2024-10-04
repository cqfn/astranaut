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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Removes comments from the DSL code.
 * @since 1.0.0
 */
public class CommentsRemover {
    /**
     * Source code in DSL, possibly containing comments.
     */
    private final String code;

    /**
     * Constructor.
     * @param code Source code in DSL, possibly containing comments
     */
    public CommentsRemover(final String code) {
        this.code = code;
    }

    /**
     * Return the source code with all comments (single and multi-line) replaced by spaces.
     * @return Uncommented source code
     */
    public String getUncommentedCode() {
        final String regex = "(?s)/\\*.*?\\*/|//.*?(?=\n|$)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(this.code);
        final StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            final String match = matcher.group();
            final String replacement = match.replaceAll("[^\\n]", " ");
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
