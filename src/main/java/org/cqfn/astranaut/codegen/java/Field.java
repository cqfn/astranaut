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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cqfn.astranaut.exceptions.BaseException;

/**
 * Describes a field and generates source code for it.
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Field implements Entity {
    /**
     * Type of the field.
     */
    private final String type;

    /**
     * Name of the field.
     */
    private final String name;

    /**
     * Initial value of the field.
     */
    private String initial;

    /**
     * Documentation.
     */
    private final JavaDoc doc;

    /**
     * Flag indicating that the generated field is public.
     */
    private boolean pub;

    /**
     * Flag indicating that the generated field is protected.
     */
    private boolean prt;

    /**
     * Flag indicating that the generated field is private.
     */
    private boolean pvt;

    /**
     * Flag indicating that the generated field is static.
     */
    private boolean stat;

    /**
     * Flag indicating that the generated field is final.
     */
    private boolean fin;

    /**
     * Constructor.
     * @param type Type of the field.
     * @param name Name of the field.
     * @param brief Brief description of the field
     */
    public Field(final String type, final String name, final String brief) {
        this.type = type;
        this.name = name;
        this.doc = new JavaDoc(brief);
        this.initial = "";
    }

    /**
     * Makes the field public.
     */
    public void makePublic() {
        this.pub = true;
        this.prt = false;
        this.pvt = false;
    }

    /**
     * Makes the field protected.
     */
    public void makeProtected() {
        this.pub = false;
        this.prt = true;
        this.pvt = false;
    }

    /**
     * Makes the field private.
     */
    public void makePrivate() {
        this.pub = false;
        this.prt = false;
        this.pvt = true;
    }

    /**
     * Makes the field static.
     */
    public void makeStatic() {
        this.stat = true;
    }

    /**
     * Makes the field final.
     * @param value Initial value of the field
     */
    public void makeFinal(final String value) {
        this.fin = true;
        this.initial = value;
    }

    /**
     * Sets the initial value of the field.
     * @param value Initial value of the field
     */
    public void setInitial(final String value) {
        this.initial = value;
    }

    /**
     * Returns the priority of the field.
     *  Fields with higher priority are placed at the beginning of classes.
     * @return Priority of the field
     */
    public int getPriority() {
        final int priority;
        if (this.stat && this.pub) {
            priority = 4;
        } else if (this.stat) {
            priority = 3;
        } else if (this.pub) {
            priority = 2;
        } else {
            priority = 1;
        }
        return priority;
    }

    @Override
    public void build(final int indent, final SourceCodeBuilder code) throws BaseException {
        this.doc.build(indent, code);
        final String declaration = this.composeDeclaration();
        if (this.initial.isEmpty()) {
            code.add(indent, declaration.concat(";"));
        } else {
            this.buildFieldWithInitialValue(indent, code, declaration);
        }
    }

    /**
     * Compiles a declaration of the field.
     * @return String declaration of the field without an initial value
     */
    private String composeDeclaration() {
        final StringBuilder builder = new StringBuilder(100);
        if (this.pub) {
            builder.append("public ");
        } else if (this.prt) {
            builder.append("protected ");
        } else if (this.pvt) {
            builder.append("private ");
        }
        if (this.stat) {
            builder.append("static ");
        }
        if (this.fin) {
            builder.append("final ");
        }
        builder.append(this.type).append(' ').append(this.name);
        return builder.toString();
    }

    /**
     * Builds field declaration with initial value.
     * @param indent Code indentation. Each generated line will be indented as follows
     * @param code Source code builder
     * @param head Field declaration without initial value
     * @throws BaseException If there are any problems during code generation
     */
    private void buildFieldWithInitialValue(final int indent, final SourceCodeBuilder code,
        final String head) throws BaseException {
        do {
            final String simple = String.format("%s = %s;", head, this.initial);
            if (SourceCodeBuilder.tryOn(indent, simple)) {
                code.add(indent, simple);
                break;
            }
            code.add(indent, String.format("%s =", head));
            final String second =  String.format("%s;", this.initial);
            if (SourceCodeBuilder.tryOn(indent + 1, second)) {
                code.add(indent + 1, second);
                break;
            }
            if (!second.contains(").")
                && Field.tryBreakLineByDepthOfCalls(indent + 1, code, second)) {
                break;
            }
            if (Field.tryBreakLineByCallChain(indent + 1, code, second)) {
                break;
            }
            throw new SourceCodeBuilder.CodeLineIsTooLong(this.initial);
        } while (false);
    }

    /**
     * Trying to break down the line of code along the call chain.
     *  Each new call in the chain starts with a new line.
     * @param indent Indentation
     * @param code Source code builder
     * @param line Line of code that should be broken into smaller lines
     * @return Result, {@code true} if successful
     * @throws BaseException If there are any problems during code generation
     */
    private static boolean tryBreakLineByCallChain(final int indent, final SourceCodeBuilder code,
        final String line) throws BaseException {
        final String[] list = line.split("(?<=\\))(?=\\.)");
        boolean result;
        do {
            result = SourceCodeBuilder.tryOn(indent, list[0]);
            if (!result) {
                break;
            }
            int index;
            for (index = 1; index < list.length && result; index = index + 1) {
                result = SourceCodeBuilder.tryOn(indent + 1, list[index]);
            }
            if (!result) {
                break;
            }
            code.add(indent, list[0]);
            for (index = 1; index < list.length; index = index + 1) {
                code.add(indent + 1, list[index]);
            }
        } while (false);
        return result;
    }

    /**
     * Tries to break down the line by the parentheses that define function calls.
     *  Each new call is made on a new line and indented.
     * @param indent Indentation
     * @param code Source code builder
     * @param line Line of code that should be broken into smaller lines
     * @return Result, {@code true} if successful
     * @throws BaseException If there are any problems during code generation
     */
    private static boolean tryBreakLineByDepthOfCalls(final int indent,
        final SourceCodeBuilder code, final String line) throws BaseException {
        final int begin = line.indexOf('(');
        final int end = line.lastIndexOf(')');
        if (begin >= 0 && end < 0) {
            throw new BaseException() {
                private static final long serialVersionUID = -1;

                @Override
                public String getInitiator() {
                    return "Codegen";
                }

                @Override
                public String getErrorMessage() {
                    return "Unclosed parenthesis";
                }
            };
        }
        boolean result = false;
        if (begin >= 0) {
            code.add(indent, line.substring(0, begin + 1).trim());
            final String middle = line.substring(begin + 1, end);
            result = Field.tryBreakLineByDepthOfCalls(indent + 1, code, middle)
                || Field.tryBreakLineByCommas(indent + 1, code, middle);
            code.add(indent, line.substring(end).trim());
        }
        return result;
    }

    /**
     * Tries to break down the line by the commas that separate the arguments.
     * @param indent Indentation
     * @param code Source code builder
     * @param line Line of code that should be broken into smaller lines
     * @return Result, {@code true} if successful
     * @throws BaseException If there are any problems during code generation
     */
    private static boolean tryBreakLineByCommas(final int indent,
        final SourceCodeBuilder code, final String line) throws BaseException {
        boolean result = true;
        final List<String> arguments = new ArrayList<>(2);
        final Pattern pattern = Pattern.compile("[^,()]+|\\([^()]*\\)");
        final Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            arguments.add(matcher.group().trim());
        }
        for (int index = 0; index < arguments.size() && result; index = index + 1) {
            final String argument;
            if (index < arguments.size() - 1) {
                argument = arguments.get(index).concat(",");
            } else {
                argument = arguments.get(index);
            }
            result = SourceCodeBuilder.tryOn(indent, argument);
            if (result) {
                code.add(indent, argument);
                continue;
            }
            result = Field.tryBreakLineByDepthOfCalls(indent, code, argument);
        }
        return result;
    }
}
