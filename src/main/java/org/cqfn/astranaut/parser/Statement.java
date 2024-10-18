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

/**
 * Some statement from the DSL source code.
 * @since 1.0.0
 */
public final class Statement {
    /**
     * Location of the statement.
     */
    private Location location;

    /**
     * Source code of the statement.
     */
    private String code;

    /**
     * Private constructor.
     */
    private Statement() {
    }

    /**
     * Returns the location of the statement.
     * @return Location of the statement
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Extracts the language marker if it is present in the statement.
     * @return Language or empty string
     */
    public String getLanguage() {
        String language = "";
        if (this.code.matches("^[a-zA-Z][a-zA-Z0-9]*:.*")) {
            language = this.code.split(":")[0].trim();
        }
        return language;
    }

    /**
     * Returns source code of the statement.
     * @return Source code of the statement
     */
    public String getCode() {
        final String language = this.getLanguage();
        String tail = this.code;
        if (!language.isEmpty()) {
            tail = this.code.substring(language.length() + 1).trim();
        }
        return tail
            .replaceAll("[\\r\\n]+", " ")
            .replaceAll("\\s{2,}", " ");
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getLocation().toString()).append(": ").append(this.getCode());
        return builder.toString();
    }

    /**
     * Statement builder.
     * @since 1.0.0
     */
    @SuppressWarnings("PMD.DataClass")
    public static final class Constructor {
        /**
         * Name of the file in which the statement is described.
         */
        private String filename;

        /**
         * Number of the first line of the statement.
         */
        private int begin;

        /**
         * Number of the last line of the statement.
         */
        private int end;

        /**
         * Source code of the statement.
         */
        private String code;

        /**
         * Public constructor.
         */
        public Constructor() {
            this.filename = "";
            this.begin = 0;
            this.end = 0;
            this.code = "";
        }

        /**
         * Sets the name of the file in which the statement is described.
         * @param name Filename
         */
        public void setFilename(final String name) {
            this.filename = name;
        }

        /**
         * Sets the number of the first line of the statement.
         * @param value Line number
         */
        public void setBegin(final int value) {
            this.begin = value;
        }

        /**
         * Sets the number of the last line of the statement.
         * @param value Line number
         */
        public void setEnd(final int value) {
            this.end = value;
        }

        /**
         * Sets the source code of the statement.
         * @param text Source code text
         */
        public void setCode(final String text) {
            this.code = text.trim();
        }

        /**
         * Verifies that all parameters are set and creates a new statement.
         * @return Statement
         */
        public Statement createStatement() {
            if (this.begin <= 0 || this.end <= 0 || this.begin > this.end || this.code.isEmpty()) {
                throw new IllegalStateException();
            }
            final Statement stmt = new Statement();
            stmt.location = new Location(this.filename, this.begin, this.end);
            stmt.code = this.code;
            return stmt;
        }
    }
}
