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
package org.cqfn.astranaut.dsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cqfn.astranaut.codegen.java.LiteralGenerator;
import org.cqfn.astranaut.codegen.java.RuleGenerator;
import org.cqfn.astranaut.core.base.Builder;
import org.cqfn.astranaut.core.base.ChildDescriptor;
import org.cqfn.astranaut.core.utils.MapUtils;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.interpreter.LiteralBuilder;

/**
 * Descriptor of a literal, that is, a node that has data and no child nodes.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.DataClass")
public final class LiteralDescriptor extends NonAbstractNodeDescriptor {
    /**
     * Common exception thrown by number parsers.
     */
    public static  final String NUMBER_EXCEPTION = "NumberFormatException";

    /**
     * Collection of primitive types.
     */
    public static final Map<String, Pair<String, String>> PRIMITIVES =
        new MapUtils<String, Pair<String, String>>()
            .put(
                "byte",
                new Pair<>("Byte.parseByte(#)", LiteralDescriptor.NUMBER_EXCEPTION)
            )
            .put(
                "short",
                new Pair<>("Short.parseShort(#)", LiteralDescriptor.NUMBER_EXCEPTION)
            )
            .put(
                "int",
                new Pair<>("Integer.parseInt(#)", LiteralDescriptor.NUMBER_EXCEPTION)
            )
            .put(
                "long",
                new Pair<>("Long.parseLong(#)", LiteralDescriptor.NUMBER_EXCEPTION)
            )
            .put(
                "float",
                new Pair<>("Float.parseFloat(#)", LiteralDescriptor.NUMBER_EXCEPTION)
            )
            .put(
                "double",
                new Pair<>("Double.parseDouble(#)", LiteralDescriptor.NUMBER_EXCEPTION)
            )
            .put(
                "char",
                new Pair<>("#.charAt(0)", "IndexOutOfBoundsException")
            )
            .put(
                "boolean",
                new Pair<>("Boolean.parseBoolean(value)", "")
            )
            .make();

    /**
     * Delimiter that is used when stringifying a descriptor.
     */
    private static final String DELIMITER = ", '";

    /**
     * Native Java type of the literal.
     */
    private String type;

    /**
     * Initial data.
     */
    private String initial;

    /**
     * Line of Java code that represents data within a node as a string.
     */
    private String serializer;

    /**
     * Line of Java code that parses data represented as a string into a native Java type.
     */
    private String parser;

    /**
     * Java exception thrown when attempting to set invalid data and which must be caught.
     */
    private String exception;

    /**
     * Constructor.
     * @param name Name of the type of the node (left side of the rule)
     */
    private LiteralDescriptor(final String name) {
        super(name);
    }

    /**
     * Returns the data type of the value that is stored in the node.
     * @return Data type
     */
    public String getDataType() {
        return this.type;
    }

    /**
     * Returns the initial data of the nodes to be created.
     * @return Data represented as a string
     */
    public String getInitial() {
        return this.initial;
    }

    /**
     * Returns line of Java code that represents data within a node as a string.
     * @return Source code of serializer
     */
    public String getSerializer() {
        final String code;
        if (this.serializer.isEmpty() && LiteralDescriptor.PRIMITIVES.containsKey(this.type)) {
            code = "String.valueOf(#)";
        } else {
            code = this.serializer;
        }
        return code;
    }

    /**
     * Returns line of Java code that parses data represented as a string into a native Java type.
     * @return Source code of parser
     */
    public String getParser() {
        final String code;
        if (this.parser.isEmpty() && LiteralDescriptor.PRIMITIVES.containsKey(this.type)) {
            code = LiteralDescriptor.PRIMITIVES.get(this.type).getKey();
        } else {
            code = this.parser;
        }
        return code;
    }

    /**
     * Returns the name of the exception class that can be thrown when processing invalid data.
     * @return Exception class name
     */
    public String getException() {
        final String code;
        if (this.exception.isEmpty() && LiteralDescriptor.PRIMITIVES.containsKey(this.type)) {
            code = LiteralDescriptor.PRIMITIVES.get(this.type).getValue();
        } else {
            code = this.exception;
        }
        return code;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append(" <- '").append(this.type).append('\'');
        if (!this.initial.isEmpty()) {
            builder.append(LiteralDescriptor.DELIMITER).append(this.initial).append('\'');
        }
        if (!this.serializer.isEmpty()) {
            builder.append(LiteralDescriptor.DELIMITER)
                .append(this.serializer)
                .append("', '")
                .append(this.parser)
                .append('\'');
        }
        if (!this.exception.isEmpty()) {
            builder.append(LiteralDescriptor.DELIMITER).append(this.exception).append('\'');
        }
        return builder.toString();
    }

    @Override
    public RuleGenerator createGenerator() {
        return new LiteralGenerator(this);
    }

    @Override
    public List<ChildDescriptor> getChildTypes() {
        return Collections.emptyList();
    }

    @Override
    public Builder createBuilder() {
        return new LiteralBuilder(this);
    }

    /**
     * Class that correctly constructs a literal descriptor.
     * @since 1.0.0
     */
    @SuppressWarnings("PMD.DataClass")
    public static final class Constructor {
        /**
         * Name of the type of the node (left side of the rule).
         */
        private final String name;

        /**
         * Native Java type of the literal.
         */
        private String type;

        /**
         * Initial data.
         */
        private String initial;

        /**
         * Line of Java code that represents data within a node as a string.
         */
        private String serializer;

        /**
         * Line of Java code that parses data represented as a string into a native Java type.
         */
        private String parser;

        /**
         * Java exception thrown when attempting to set invalid data and which must be caught.
         */
        private String exception;

        /**
         * Constructor.
         * @param name Name of the type of the node (left side of the rule)
         */
        public Constructor(final String name) {
            this.name = name;
            this.type = "";
            this.initial = "";
            this.serializer = "";
            this.parser = "";
            this.exception = "";
        }

        /**
         * Return native Java type of the literal.
         * @return Literal type.
         */
        public String getType() {
            return this.type;
        }

        /**
         * Returns a flag if the literal type is a primitive Java type.
         * @return Flag
         */
        public boolean hasPrimitiveType() {
            return LiteralDescriptor.PRIMITIVES.containsKey(this.type);
        }

        /**
         * Sets native Java type of the literal.
         * @param value Native Java type of the literal
         */
        public void setType(final String value) {
            this.type = value.trim();
        }

        /**
         * Returns a flag whether the initial value of the node is specified.
         * @return Flag
         */
        public boolean hasInitial() {
            return !this.initial.isEmpty();
        }

        /**
         * Sets the initial data that the nodes to be created will have
         *  if no other data is specified.
         * @param value Initial data
         */
        public void setInitial(final String value) {
            this.initial = value.trim();
        }

        /**
         * Sets the serializer.
         * @param value Line of Java code that represents data within a node as a string
         */
        public void setSerializer(final String value) {
            this.serializer = value.trim();
        }

        /**
         * Returns a flag whether a parser is specified.
         * @return Flag
         */
        public boolean hasParser() {
            return !this.parser.isEmpty();
        }

        /**
         * Sets the parser.
         * @param value Line of Java code that parses data represented as a string
         *  into a native Java type.
         */
        public void setParser(final String value) {
            this.parser = value.trim();
        }

        /**
         * Sets the exception name thrown by the parser.
         * @param value Java exception (class name) thrown when attempting to set invalid
         *  data and which must be caught
         */
        public void setException(final String value) {
            this.exception = value.trim();
        }

        /**
         * Checks that all data is valid and therefore a literal can be created.
         * @return Checking result
         */
        public boolean isValid() {
            boolean result = false;
            do {
                if (this.type.isEmpty()) {
                    break;
                }
                if (this.initial.isEmpty() && !this.serializer.isEmpty()) {
                    break;
                }
                if (this.serializer.isEmpty() ^ this.parser.isEmpty()) {
                    break;
                }
                if (this.parser.isEmpty() && !this.exception.isEmpty()) {
                    break;
                }
                result = true;
            } while (false);
            return result;
        }

        /**
         * Checks that all data is valid and, if so, creates a new descriptor with that data.
         * @return A new literal descriptor
         */
        public LiteralDescriptor createDescriptor() {
            if (!this.isValid()) {
                throw new IllegalStateException();
            }
            final LiteralDescriptor descriptor = new LiteralDescriptor(this.name);
            descriptor.type = this.type;
            descriptor.initial = this.initial;
            descriptor.serializer = this.serializer;
            descriptor.parser = this.parser;
            descriptor.exception = this.exception;
            return descriptor;
        }

        @Override
        public String toString() {
            return String.format(
                "%s <- %s",
                this.name,
                String.join(
                    ", ",
                    Arrays.asList(
                        Constructor.format(this.type),
                        Constructor.format(this.initial),
                        Constructor.format(this.serializer),
                        Constructor.format(this.parser),
                        Constructor.format(this.exception)
                    )
                )
            );
        }

        /**
         * Formats a parameter for pretty stringification of constructor data.
         * @param parameter Parameter
         * @return Formatted parameter
         */
        private static String format(final String parameter) {
            final String result;
            if (parameter.isEmpty()) {
                result = "?";
            } else {
                result = String.format("'%s'", parameter);
            }
            return result;
        }
    }
}
