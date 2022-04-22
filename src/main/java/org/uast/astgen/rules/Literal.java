/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.rules;

import java.util.Objects;

/**
 * A rule that describes literal.
 *
 * @since 1.0
 */
@SuppressWarnings("PMD.DataClass")
public final class Literal extends Vertex {
    /**
     * Left part.
     */
    private String type;

    /**
     * The name of the native type (class) for storing data.
     */
    private String klass;

    /**
     * Native method to convert data to string.
     */
    private String stringifier;

    /**
     * Native method to convert data from string.
     */
    private String parser;

    /**
     * Name of the exception class that can be thrown when converting a string to data.
     */
    private String exception;

    /**
     * Private constructor.
     */
    private Literal() {
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean isOrdinary() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    /**
     * Returns the name of the native type (class) for storing data.
     * @return The native type name
     */
    public String getKlass() {
        return this.klass;
    }

    /**
     * Returns the native method to convert data to string.
     * @return The code
     */
    public String getStringifier() {
        return this.stringifier;
    }

    /**
     * Returns the native method to convert data from string.
     * @return The code
     */
    public String getParser() {
        return this.parser;
    }

    /**
     * Returns the name of the exception class that can be thrown when converting
     * a string to data.
     * @return The native type name
     */
    public String getException() {
        return this.exception;
    }

    @Override
    public String toString() {
        final String result;
        if (this.exception.isEmpty()) {
            result = String.format(
                "%s <- $%s$, $%s$, $%s$",
                this.type,
                this.klass,
                this.stringifier,
                this.parser
            );
        } else {
            result = String.format(
                "%s <- $%s$, $%s$, $%s$, $%s$",
                this.type,
                this.klass,
                this.stringifier,
                this.parser,
                this.exception
            );
        }
        return result;
    }

    /**
     * Builder for literal building.
     *
     * @since 1.0
     */
    public static class Builder {
        /**
         * Left part.
         */
        private String type = "";

        /**
         * The name of the native type (class) for storing data.
         */
        private String klass = "";

        /**
         * Native method to convert data to string.
         */
        private String stringifier = "";

        /**
         * Native method to convert data from string.
         */
        private String parser = "";

        /**
         * Name of the exception class that can be thrown when converting a string to data.
         */
        private String exception = "";

        /**
         * Sets the type name.
         * @param value Type name
         */
        public void setType(final String value) {
            this.type = Objects.requireNonNull(value);
        }

        /**
         * Sets the name of the native type (class) for storing data.
         * @param value The name of native type
         */
        public void setKlass(final String value) {
            this.klass = Objects.requireNonNull(value);
        }

        /**
         * Sets the native method to convert data to string.
         * @param value The code
         */
        public void setStringifier(final String value) {
            this.stringifier = Objects.requireNonNull(value);
        }

        /**
         * Sets the native method to convert data from string.
         * @param value The code
         */
        public void setParser(final String value) {
            this.parser = Objects.requireNonNull(value);
        }

        /**
         * Sets the name of the exception class that can be thrown when converting
         * a string to data.
         * @param value The native type name
         */
        public void setException(final String value) {
            this.exception = Objects.requireNonNull(value);
        }

        /**
         * Checks the builder is in valid state (can build a literal).
         * @return Checking result.
         */
        public boolean isValid() {
            return !this.type.isEmpty() && !this.klass.isEmpty() && !this.stringifier.isEmpty()
                && !this.parser.isEmpty();
        }

        /**
         * Builds a literal from the collected parameters.
         * @return A literal object
         */
        public Literal build() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final Literal obj = new Literal();
            obj.type = this.type;
            obj.klass = this.klass;
            obj.stringifier = this.stringifier;
            obj.parser = this.parser;
            obj.exception = this.exception;
            return  obj;
        }
    }
}
