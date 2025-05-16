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

import guru.nidi.graphviz.engine.GraphvizException;
import java.io.File;
import java.io.IOException;
import org.cqfn.astranaut.core.base.Tree;
import org.cqfn.astranaut.core.utils.FilesWriter;
import org.cqfn.astranaut.core.utils.JsonSerializer;
import org.cqfn.astranaut.core.utils.TreeVisualizer;
import org.cqfn.astranaut.core.utils.visualizer.WrongFileExtension;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Basic methods that are suitable for any action.
 * @since 1.0.0
 */
class BaseAction {
    /**
     * Writes a file.
     * @param file File
     * @param content File content
     * @throws CliException In case it is not possible to write a file.
     */
    protected void writeFile(final File file, final String content) throws CliException {
        this.getClass();
        final boolean result = new FilesWriter(file.getAbsolutePath()).writeStringNoExcept(content);
        if (!result) {
            throw new CannotWriteFile(file.getName());
        }
    }

    /**
     * Writes the transformation result to a JSON file and/or an image file,
     * based on the provided options.
     * <p>
     *     If the resulting tree path is specified in the options, the method attempts to serialize
     *     the tree to a JSON file. If serialization fails, a {@link CannotWriteFile}
     *     exception is thrown.
     * </p>
     * <p>
     *     If the resulting image path is specified, the method attempts to generate
     *     a visualization of the tree and save it as an image. Failures in writing or using
     *     an invalid extension are reported via exceptions.
     * </p>
     *
     * @param tree The tree to be serialized and/or visualized
     * @param options The transformation output options including file paths
     * @throws BaseException If the output cannot be written or the file extension is invalid
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    protected void writeTransformationResult(final Tree tree, final TransformerArguments options)
        throws BaseException {
        this.getClass();
        if (!options.getResultingTreePath().isEmpty()) {
            final JsonSerializer serializer = new JsonSerializer(tree);
            final boolean flag = serializer.serializeToFile(options.getResultingTreePath());
            if (!flag) {
                throw new CannotWriteFile(options.getResultingTreePath());
            }
        }
        if (!options.getResultingImagePath().isEmpty()) {
            final TreeVisualizer visualizer = new TreeVisualizer(tree);
            try {
                visualizer.visualize(new File(options.getResultingImagePath()));
            } catch (final WrongFileExtension exception) {
                throw new CommonCliException(exception.getErrorMessage());
            } catch (final GraphvizException | IOException ignored) {
                throw new CannotWriteFile(options.getResultingImagePath());
            }
        }
    }

    /**
     * Exception 'Cannot write file'.
     * @since 1.0.0
     */
    protected static final class CannotWriteFile extends CliException {
        /**
         * Version identifier.
         */
        private static final long serialVersionUID = -1;

        /**
         * The name of the file that could not be written.
         */
        private final String name;

        /**
         * Constructor.
         * @param name The name of the file that could not be written
         */
        protected CannotWriteFile(final String name) {
            this.name = name;
        }

        @Override
        public String getErrorMessage() {
            return String.format("Cannot write file: '%s'", this.name);
        }
    }
}
