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
package org.cqfn.astranaut.utils.cli;

import java.util.Arrays;
import java.util.List;

/**
 * Custom implementation of CLI file parameter converter for the '--rules' option.
 *
 * @since 0.1.5
 */
public final class RulesFileConverter extends BaseFileConverter {
    /**
     * The list of valid file extensions.
     */
    private static final List<String> VALID_EXT = Arrays.asList("txt", "dsl");

    /**
     * Constructor.
     * @param option An option name
     */
    public RulesFileConverter(final String option) {
        super(option);
    }

    @Override
    public List<String> getValidExtensions() {
        return RulesFileConverter.VALID_EXT;
    }

    @Override
    public boolean fileMustExist() {
        return true;
    }
}
