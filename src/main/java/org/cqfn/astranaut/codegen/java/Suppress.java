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

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Entity that suppresses compiler or codechecker warnings. Since we generate
 *  (non-)perfect code, we sometimes need it.
 * @since 1.0.0
 */
public final class Suppress implements Entity {
    /**
     * Set of warnings to be suppressed.
     */
    private final Set<String> set;

    /**
     * Constructor.
     */
    public Suppress() {
        this.set = new TreeSet<>();
    }

    /**
     * Adds a warning that needs to be suppressed.
     * @param warning Warning
     */
    public void addWarning(final String warning) {
        this.set.add(warning);
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        final int size = this.set.size();
        if (size > 1) {
            code.add(
                indent,
                String.format(
                    "@SuppressWarnings({%s})",
                    this.set
                        .stream()
                        .map(s -> String.format("\"%s\"", s))
                        .collect(Collectors.joining(", "))
                )
            );
        } else if (size == 1) {
            code.add(
                indent,
                String.format("@SuppressWarnings(\"%s\")", this.set.iterator().next())
            );
        }
    }
}
