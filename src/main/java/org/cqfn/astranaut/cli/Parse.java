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
package org.cqfn.astranaut.cli;

import java.util.List;
import org.cqfn.astranaut.core.base.Transformer;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.parsing.FileSource;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Parses source code using the described rules.
 * @since 1.0.0
 */
public final class Parse extends BaseAction implements Action {
    /**
     * The instance.
     */
    public static final Action INSTANCE = new Parse();

    /**
     * Private constructor.
     */
    private Parse() {
    }

    @Override
    public void perform(final Program program, final List<String> args) throws BaseException {
        final TransformerArguments options = new TransformerArguments();
        options.parse(args);
        final FileSource source = new FileSource(options.getSourceFilePath());
        final Tree before = source.parseIntoTree();
        final Transformer transformer = program.getTransformer(options.getLanguage());
        final Tree after = transformer.transform(before);
        this.writeTransformationResult(after, options);
    }
}
