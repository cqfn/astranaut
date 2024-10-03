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
 * Describes a method and generates source code for it.
 * @since 1.0.0
 */
public final class Method implements Entity {
    /**
     * Type of return value.
     */
    private final String ret;

    /**
     * Name of the method.
     */
    private final String name;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * Flag indicating that the generated method is overridden.
     */
    private final boolean over;

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
     * Flag indicating that the generated method is static.
     */
    private boolean stat;

    /**
     * Flag indicating that the generated method is final.
     */
    private boolean fin;

    /**
     * Body of the method.
     */
    private String body;

    /**
     * Constructor of overridden method.
     * @param ret Type of the method.
     * @param name Name of the method.
     */
    public Method(final String ret, final String name) {
        this(ret, name, "");
    }

    /**
     * Constructor.
     * @param ret Type of the method.
     * @param name Name of the method.
     * @param brief Brief description of the method
     */
    public Method(final String ret, final String name, final String brief) {
        this.ret = ret;
        this.name = name;
        this.doc = new JavaDoc(brief);
        this.over = brief.isEmpty();
        this.body = "";
    }

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
     * Makes the method static.
     */
    public void makeStatic() {
        this.stat = true;
    }

    /**
     * Makes the method final.
     */
    public void makeFinal() {
        this.fin = true;
    }

    /**
     * Sets the body of the method.
     * @param text Method body source code
     */
    public void setBody(final String text) {
        this.body = text;
    }

    /**
     * Returns the priority of the method.
     *  Fields with higher priority are placed at the beginning of classes.
     * @return Priority of the method
     */
    public int getPriority() {
        final int priority;
        if (!this.stat && this.pub) {
            priority = 4;
        } else if (this.pub) {
            priority = 3;
        } else if (this.stat) {
            priority = 1;
        } else {
            priority = 2;
        }
        return priority;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        if (this.doc.hasNonEmptyBrief()) {
            this.doc.build(indent, code);
        }
        if (this.over) {
            code.add(indent, "@Override");
        }
        final StringBuilder header = new StringBuilder(128);
        if (this.pub) {
            header.append("public ");
        } else if (this.prt) {
            header.append("protected ");
        } else if (this.pvt) {
            header.append("private ");
        }
        if (this.stat) {
            header.append("static ");
        }
        if (this.fin) {
            header.append("final ");
        }
        header.append(this.ret).append(' ').append(this.name).append("() {");
        code.add(indent, header.toString());
        this.buildBody(indent + 1, code);
        code.add(indent, "}");
    }

    /**
     * Generates the body of the method. Splits it into lines and indents it properly.
     * @param indent Code indentation. Each generated line will be indented as follows
     * @param code Source code builder
     * @throws BaseException If there are any problems during code generation
     */
    public void buildBody(final int indent, final SourceCodeBuilder code) throws BaseException {
        final List<Pair<String, Integer>> lines = this.splitBodyByLines();
        for (final Pair<String, Integer> line : lines) {
            code.add(indent + line.getValue(), line.getKey());
        }
    }

    /**
     * Separates the method body by indented lines.
     * @return List of indented lines (key is line, value is indentation)
     * @throws SyntaxErrorInSourceCode If the method body contains an error that could be detected
     *  at this stage
     */
    public List<Pair<String, Integer>> splitBodyByLines() throws SyntaxErrorInSourceCode {
        final List<Pair<String, Integer>> list = new ArrayList<>(0);
        int indent = 0;
        for (final String line : this.body.split("\n")) {
            String tail = line.trim();
            while (!tail.isEmpty()) {
                int extra = 0;
                if (tail.charAt(0) == '.') {
                    extra = 1;
                }
                int index = tail.indexOf('{');
                if (index >= 0) {
                    list.add(new Pair<>(tail.substring(0, index + 1).trim(), indent + extra));
                    indent = indent + 1;
                    tail = tail.substring(index + 1).trim();
                    continue;
                }
                index = tail.indexOf(';');
                if (index >= 0) {
                    list.add(new Pair<>(tail.substring(0, index + 1).trim(), indent + extra));
                    tail = tail.substring(index + 1).trim();
                    continue;
                }
                index = tail.indexOf('}');
                if (index > 0) {
                    throw new SyntaxErrorInSourceCode(tail);
                }
                if (index == 0) {
                    indent = indent - 1;
                    list.add(new Pair<>("}", indent));
                    tail = tail.substring(index + 1).trim();
                    continue;
                }
                list.add(new Pair<>(tail, indent + extra));
                tail = "";
            }
        }
        return list;
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
