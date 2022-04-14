/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.Objects;
import org.uast.astgen.utils.StringUtils;

/**
 * Java class field.
 *
 * @since 1.0
 */
public final class Field implements Entity {
    /**
     * End of line string constant.
     */
    private static final String EOL = ";\n";

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
    private String init;

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
        this.init = "";
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
        this.init = Objects.requireNonNull(expr);
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(64);
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.brief)
            .append(".\n")
            .append(tabulation)
            .append(" */\n");
        final StringBuilder declaration = new StringBuilder().append(tabulation);
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
        final StringBuilder copy = new StringBuilder(declaration.toString());
        if (!this.init.isEmpty()) {
            declaration.append(" = ").append(this.init);
        }
        declaration.append(Field.EOL);
        String result = declaration.toString();
        if (result.length() > Entity.MAX_LINE_LENGTH) {
            this.generateInit(copy, indent + 1);
            result = copy.toString();
        }
        builder.append(result);
        return builder.toString();
    }

    /**
     * Generates init expression (case if it takes more than one line).
     * @param builder Where to generate
     * @param indent Indentation
     */
    private void generateInit(final StringBuilder builder, final int indent) {
        builder.append(" =");
        final String[] lines = this.init.replace("(", "(\n")
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
}
