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

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.cqfn.astranaut.core.base.Transformer;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.core.utils.JsonDeserializer;
import org.cqfn.astranaut.core.utils.JsonSerializer;
import org.cqfn.astranaut.core.utils.TreeVisualizer;
import org.cqfn.astranaut.core.utils.visualizer.WrongFileExtension;
import org.cqfn.astranaut.dsl.Program;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Transforms syntax tree using the described rules.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.PreserveStackTrace")
public final class Transform extends BaseAction implements Action {
    /**
     * The instance.
     */
    public static final Action INSTANCE = new Transform();

    /**
     * Private constructor.
     */
    private Transform() {
    }

    @Override
    public void perform(final Program program, final List<String> args) throws BaseException {
        final TransformerArguments options = new TransformerArguments();
        options.parse(args);
        final String source = new FilesReader(options.getSourceFilePath())
            .readAsString(
                (FilesReader.CustomExceptionCreator<BaseException>) () -> new CommonCliException(
                    String.format(
                        "Can't read source tree from '%s'",
                        options.getSourceFilePath()
                    )
                )
            );
        final JsonDeserializer deserializer = new JsonDeserializer(source, program);
        final Tree before = deserializer.convert();
        final Transformer transformer = program.getTransformer(options.getLanguage());
        final Tree after = transformer.transform(before);
        if (!options.getResultingTreePath().isEmpty()) {
            final JsonSerializer serializer = new JsonSerializer(after);
            final boolean flag = serializer.serializeToFile(options.getResultingTreePath());
            if (!flag) {
                throw new CannotWriteFile(options.getResultingTreePath());
            }
        }
        if (!options.getResultingImagePath().isEmpty()) {
            final TreeVisualizer visualizer = new TreeVisualizer(after);
            try {
                visualizer.visualize(new File(options.getResultingImagePath()));
            } catch (final IOException ignored) {
                throw new CannotWriteFile(options.getResultingImagePath());
            } catch (final WrongFileExtension exception) {
                throw new CommonCliException(exception.getErrorMessage());
            }
        }
    }
}
