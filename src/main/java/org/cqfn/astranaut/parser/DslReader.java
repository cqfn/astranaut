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

import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Reads the DSL source code and separates it into statements.
 * @since 1.0.0
 */
public final class DslReader {
    /**
     * Set of processed file names to avoid processing the same file twice.
     */
    private final Set<String> files;

    /**
     * Reader of the imported file.
     */
    private DslReader imported;

    /**
     * Name of the file containing the DSL source code.
     */
    private String filename;

    /**
     * Source code lines.
     */
    private String[] lines;

    /**
     * Current line number.
     */
    private int current;

    /**
     * Constructor.
     */
    public DslReader() {
        this(new TreeSet<>());
    }

    /**
     * Private constructor.
     * @param files Set of processed file names to avoid processing the same file twice
     */
    private DslReader(final Set<String> files) {
        this.files = files;
        this.filename = "";
        this.lines = new String[0];
        this.current = 0;
    }

    /**
     * Sets the source code to be divided into statements.
     * @param code DSL source code
     */
    public void setSourceCode(final String code) {
        this.setFileNameAndSourceCode("", code);
    }

    /**
     * Reads a file containing DSL source code.
     * @param name File name
     * @throws BaseException If the source code file cannot be read
     */
    public void readFile(final String name) throws BaseException {
        final String absolute = Paths.get(name).toAbsolutePath().toString();
        if (this.files.contains(absolute)) {
            this.setSourceCode("");
        } else {
            final FilesReader reader = new FilesReader(absolute);
            final String code = reader.readAsString(
                (FilesReader.CustomExceptionCreator<BaseException>) () -> new BaseException() {
                    @Override
                    public String getInitiator() {
                        return "Parser";
                    }

                    @Override
                    public String getErrorMessage() {
                        return String.format("Can't read '%s'", name);
                    }
                }
            );
            this.setFileNameAndSourceCode(absolute, code);
        }
    }

    /**
     * Reads and returns the next statement.
     * @return Statement or {@code null} if there are no more statements
     */
    public Statement getStatement() {
        Statement stmt = null;
        if (this.imported != null) {
            stmt = this.imported.getStatement();
        }
        if (stmt == null && this.current < this.lines.length) {
            stmt = this.extractStatement();
        }
        return stmt;
    }

    /**
     * Sets the file name and the source code to be divided into statements.
     * @param name Name of the file containing the DSL source code
     * @param code DSL source code
     */
    private void setFileNameAndSourceCode(final String name, final String code) {
        this.filename = name;
        final String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            this.lines = new String[0];
        } else {
            this.lines = trimmed.split("\n");
        }
        this.current = 0;
    }

    /**
     * Extracts the next statement.
     * @return Statement or {@code null} if there are no more statements
     */
    private Statement extractStatement() {
        final Statement.Constructor ctor = new Statement.Constructor();
        ctor.setFilename(this.filename);
        final StringBuilder builder = new StringBuilder();
        while (this.current < this.lines.length) {
            ctor.setEnd(this.current + 1);
            final String line = this.lines[this.current].trim();
            if (line.isEmpty()) {
                this.current = this.current + 1;
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            } else {
                ctor.setBegin(this.current + 1);
            }
            final int index = line.indexOf(';');
            if (index < 0) {
                builder.append(line);
                this.current = this.current + 1;
            } else {
                builder.append(line.substring(0, index));
                this.lines[this.current] = line.substring(index + 1);
                break;
            }
        }
        final String code = builder.toString().trim();
        final Statement stmt;
        if (code.isEmpty()) {
            stmt = null;
        } else {
            ctor.setCode(code);
            stmt = ctor.createStatement();
        }
        return stmt;
    }
}
