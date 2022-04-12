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
            .append(" */\n")
            .append(tabulation);
        if (this.fprivate) {
            builder.append("private ");
        } else if (this.fpublic) {
            builder.append("public ");
        }
        if (this.fstatic) {
            builder.append("static ");
        }
        if (this.ffinal) {
            builder.append("final ");
        }
        builder.append(this.type).append(' ').append(this.name);
        if (!this.init.isEmpty()) {
            builder.append(" = ").append(this.init);
        }
        builder.append(";\n");
        return builder.toString();
    }
}
