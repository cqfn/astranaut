/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Ivan Kniazkov
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

import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.utils.StringUtils;

/**
 * Java class field.
 *
 * @since 0.1.5
 */
public final class Field implements Entity {
    /**
     * End of line string constant.
     */
    private static final String EOL = ";\n";

    /**
     * Assignment construction.
     */
    private static final String ASSIGN = " = ";

    /**
     * The brief description.
     */
    private final String brief;

    /**
     * The type name.
     */
    private final String type;

    /**
     * The name.
     */
    private final String name;

    /**
     * The flag indicates that the field is public.
     */
    private boolean fpublic;

    /**
     * The flag indicates that the field is private.
     */
    private boolean fprivate;

    /**
     * The flag indicates that the field is static.
     */
    private boolean fstatic;

    /**
     * The flag indicates that the field is final.
     */
    private boolean ffinal;

    /**
     * Expression that initializes the field.
     */
    private List<String> init;

    /**
     * Constructor.
     * @param brief The brief description
     * @param type The type name
     * @param name The name
     */
    public Field(final String brief, final String type, final String name) {
        this.brief = brief;
        this.type = type;
        this.name = name;
        this.fprivate = true;
        this.init = Collections.emptyList();
    }

    /**
     * Returns the field name.
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the flag indicates that the field is static.
     * @return The flag
     */
    public boolean isStatic() {
        return this.fstatic;
    }

    /**
     * Resets all flags.
     */
    public void resetFlags() {
        this.fpublic = false;
        this.fprivate = false;
        this.fstatic = false;
        this.ffinal = false;
    }

    /**
     * Makes this field public.
     */
    public void makePublic() {
        this.fpublic = true;
        this.fprivate = false;
    }

    /**
     * Makes this field private.
     */
    public void makePrivate() {
        this.fpublic = false;
        this.fprivate = true;
    }

    /**
     * Makes this field static and final.
     */
    public void makeStaticFinal() {
        this.fstatic = true;
        this.ffinal = true;
    }

    /**
     * Makes this field final.
     */
    public void makeFinal() {
        this.ffinal = true;
    }

    /**
     * Sets the expression that initializes the field.
     * @param expr The expression
     */
    public void setInitExpr(final String expr) {
        this.init = Collections.singletonList(expr);
    }

    /**
     * Sets the expression that initializes the field.
     * @param list The expression that takes several lines
     */
    public void setInitExpr(final List<String> list) {
        this.init = list;
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(256);
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.brief)
            .append(".\n")
            .append(tabulation)
            .append(" */\n");
        final String declaration = tabulation.concat(this.generateDeclaration());
        if (this.init.isEmpty()) {
            builder.append(declaration).append(Field.EOL);
        } else if (this.init.size() == 1) {
            final StringBuilder line = new StringBuilder();
            line.append(declaration).append(Field.ASSIGN)
                .append(this.init.get(0)).append(Field.EOL);
            String result = line.toString();
            if (result.length() > Entity.MAX_LINE_LENGTH) {
                final StringBuilder multiline = new StringBuilder();
                multiline.append(declaration);
                this.generateInitFromSingleLine(multiline, indent + 1);
                result = multiline.toString();
            }
            builder.append(result);
        } else {
            builder.append(declaration);
            this.generateInitFromList(builder, indent + 1);
        }
        return builder.toString();
    }

    /**
     * Generates field declaration, without init expression.
     * @return Field declaration
     */
    private String generateDeclaration() {
        final StringBuilder declaration = new StringBuilder(32);
        if (this.fprivate) {
            declaration.append("private ");
        } else if (this.fpublic) {
            declaration.append("public ");
        }
        if (this.fstatic) {
            declaration.append("static ");
        }
        if (this.ffinal) {
            declaration.append("final ");
        }
        declaration.append(this.type).append(' ').append(this.name);
        return declaration.toString();
    }

    /**
     * Generates init expression from single line (case if it really
     *  takes more than one line).
     * @param builder Where to generate
     * @param indent Indentation
     */
    private void generateInitFromSingleLine(final StringBuilder builder, final int indent) {
        builder.append(" =");
        final String[] lines = this.init.get(0).replace("(", "(\n")
            .replace(")", "\n)")
            .replace(",", ",\n")
            .split("\n");
        int offset = 0;
        for (int index = 0; index < lines.length; index = index + 1) {
            final String line = lines[index].trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == ')') {
                offset = offset - 1;
            }
            builder.append('\n')
                .append(StringUtils.SPACE.repeat((indent + offset) * Entity.TAB_SIZE))
                .append(line);
            if (line.endsWith("(")) {
                offset = offset + 1;
            }
        }
        builder.append(Field.EOL);
    }

    /**
     * Generates init expression from list of lines.
     * @param builder Where to generate
     * @param indent Indentation
     */
    private void generateInitFromList(final StringBuilder builder, final int indent) {
        builder.append(Field.ASSIGN);
        for (int index = 0; index < this.init.size(); index = index + 1) {
            String line = this.init.get(index);
            int gap = 0;
            if (line.startsWith("\t")) {
                final int len = line.length();
                for (int symbol = 0; symbol < len; symbol = symbol + 1) {
                    if (line.charAt(symbol) == '\t') {
                        gap = gap + 1;
                    } else {
                        break;
                    }
                }
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (index > 0) {
                builder.append('\n')
                    .append(StringUtils.SPACE.repeat((indent + gap) * Entity.TAB_SIZE));
            }
            builder.append(line);
        }
        builder.append(Field.EOL);
    }
}
