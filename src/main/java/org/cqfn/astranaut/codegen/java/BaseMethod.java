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

import java.util.ArrayList;
import java.util.List;
import org.cqfn.astranaut.core.utils.Pair;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Base class for methods: 'ordinary' Java method and Java constructor.
 * @since 1.0.0
 */
public abstract  class BaseMethod implements Entity {
    /**
     * Flag indicating that the generated method is public.
     */
    private boolean pub;

    /**
     * Flag indicating that the generated method is protected.
     */
    private boolean prt;

    /**
     * Flag indicating that the generated method is private.
     */
    private boolean pvt;

    /**
     * Makes the method public.
     */
    public void makePublic() {
        this.pub = true;
        this.prt = false;
        this.pvt = false;
    }

    /**
     * Makes the method protected.
     */
    public void makeProtected() {
        this.pub = false;
        this.prt = true;
        this.pvt = false;
    }

    /**
     * Makes the method private.
     */
    public void makePrivate() {
        this.pub = false;
        this.prt = false;
        this.pvt = true;
    }

    /**
     * Returns body of the method.
     * @return Body of the method
     */
    public abstract String getBody();

    /**
     * Returns flag indicating that the generated method is public.
     * @return Public flag
     */
    public boolean isPublic() {
        return this.pub;
    }

    /**
     * Returns flag indicating that the generated method is protected.
     * @return Protected flag
     */
    public boolean isProtected() {
        return this.prt;
    }

    /**
     * Returns flag indicating that the generated method is private.
     * @return Private flag
     */
    public boolean isPrivate() {
        return this.pvt;
    }

    /**
     * Generates the body of the method. Splits it into lines and indents it properly.
     * @param indent Code indentation. Each generated line will be indented as follows
     * @param code Source code builder
     * @throws BaseException If there are any problems during code generation
     */
    protected void buildBody(final int indent, final SourceCodeBuilder code) throws BaseException {
        final List<Pair<String, Integer>> lines = this.splitBodyByLines(indent);
        for (final Pair<String, Integer> line : lines) {
            code.add(indent + line.getValue(), line.getKey());
        }
    }

    /**
     * Separates the method body by indented lines.
     * @param indent Code indentation. Each generated line will be indented as follows
     * @return List of indented lines (where key is line, value is indentation)
     * @throws BaseException If the method body contains an error that could be detected
     *  at this stage or source code line is too long
     */
    private List<Pair<String, Integer>> splitBodyByLines(final int indent) throws BaseException {
        final List<Pair<String, Integer>> list = new ArrayList<>(0);
        int offset = 0;
        for (final String line : this.getBody().split("\n")) {
            String tail = line.trim();
            while (!tail.isEmpty()) {
                int extra = 0;
                if (tail.charAt(0) == '.') {
                    extra = 1;
                }
                int index = tail.indexOf('{');
                if (index > 0 && tail.charAt(0) == '}') {
                    offset = offset - 1;
                }
                if (index >= 0) {
                    list.add(new Pair<>(tail.substring(0, index + 1).trim(), offset + extra));
                    offset = offset + 1;
                    tail = tail.substring(index + 1).trim();
                    continue;
                }
                index = tail.indexOf(';');
                if (index >= 0) {
                    list.add(new Pair<>(tail.substring(0, index + 1).trim(), offset + extra));
                    tail = tail.substring(index + 1).trim();
                    continue;
                }
                index = tail.indexOf('}');
                if (index > 0) {
                    throw new SyntaxErrorInSourceCode(tail);
                }
                if (index == 0) {
                    offset = offset - 1;
                    list.add(new Pair<>("}", offset));
                    tail = tail.substring(index + 1).trim();
                    continue;
                }
                list.add(new Pair<>(tail, offset + extra));
                tail = "";
            }
        }
        return BaseMethod.fixLinesThatAreTooLong(list, indent);
    }

    /**
     * Fixes lines that are too long by breaking them into smaller lines where possible.
     * @param list Initial list of lines that may contain lines that are too long
     * @param indent Code indentation. Each generated line will be indented as follows
     * @return Fixed list of lines
     * @throws BaseException If some lines are too long and they could never be split
     */
    private static List<Pair<String, Integer>> fixLinesThatAreTooLong(
        final List<Pair<String, Integer>> list, final int indent) throws BaseException {
        List<Pair<String, Integer>> result = new ArrayList<>(list.size());
        boolean flag = false;
        for (final Pair<String, Integer> line : list) {
            final String code = line.getKey();
            final int offset = line.getValue();
            if (SourceCodeBuilder.tryOn(indent + offset, code)) {
                result.add(line);
                continue;
            }
            final int index = code.indexOf('=');
            if (index > 0) {
                result.add(new Pair<>(code.substring(0, index + 1), offset));
                result.add(new Pair<>(code.substring(index + 1).trim(), offset + 1));
                flag = true;
                continue;
            }
            throw new SourceCodeBuilder.CodeLineIsTooLong(code);
        }
        if (flag) {
            result = BaseMethod.fixLinesThatAreTooLong(result, indent);
        }
        return result;
    }

    /**
     * Exception 'Syntax error in source code'.
     * @since 1.0.0
     */
    private static final class SyntaxErrorInSourceCode extends BaseException {
        /**
         * Version identifier.
         */
        private static final long serialVersionUID = -1;

        /**
         * Text of a source code that contains an error.
         */
        private final String text;

        /**
         * Constructor.
         * @param text Text of a source code that contains an error.
         */
        private SyntaxErrorInSourceCode(final String text) {
            this.text = text;
        }

        @Override
        public String getInitiator() {
            return "Codegen";
        }

        @Override
        public String getErrorMessage() {
            return String.format(
                "Syntax error in source code: '%s'",
                this.text.trim()
            );
        }
    }
}
