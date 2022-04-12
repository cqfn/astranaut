/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.uast.astgen.utils.StringUtils;

/**
 * Java class.
 *
 * @since 1.0
 */
public final class Klass implements Entity {
    /**
     * The brief description.
     */
    private final String brief;

    /**
     * The version.
     */
    private String version;

    /**
     * The flag indicates that the class is public.
     */
    private boolean fpublic;

    /**
     * The flag indicates that the class is private.
     */
    private boolean fprivate;

    /**
     * The flag indicates that the class is static.
     */
    private boolean fstatic;

    /**
     * The flag indicates that the class is final.
     */
    private boolean ffinal;

    /**
     * The class name.
     */
    private final String name;

    /**
     * The list of fields.
     */
    private final List<Field> fields;

    /**
     * The list of methods.
     */
    private final List<Method> methods;

    /**
     * Constructor.
     * @param brief The brief description
     * @param name The class name
     */
    public Klass(final String brief, final String name) {
        this.brief = brief;
        this.version = "1.0";
        this.fpublic = true;
        this.name = name;
        this.fields = new ArrayList<>(0);
        this.methods = new ArrayList<>(0);
    }

    /**
     * Specifies the class version.
     * @param str The version
     */
    public void setVersion(final String str) {
        this.version = Objects.requireNonNull(str);
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
     * Makes this class public.
     */
    public void makePublic() {
        this.fpublic = true;
        this.fprivate = false;
    }

    /**
     * Makes this class private.
     */
    public void makePrivate() {
        this.fpublic = false;
        this.fprivate = true;
    }

    /**
     * Makes this class static.
     */
    public void makeStatic() {
        this.fstatic = true;
    }

    /**
     * Makes this field final.
     */
    public void makeFinal() {
        this.ffinal = true;
    }

    /**
     * Adds the field to the class.
     * @param field The field
     */
    public void addField(final Field field) {
        this.fields.add(field);
    }

    /**
     * Adds the method to the class.
     * @param method The method
     */
    public void addMethod(final Method method) {
        this.methods.add(method);
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(128);
        builder.append(tabulation)
            .append("/**\n")
            .append(tabulation)
            .append(" * ")
            .append(this.brief)
            .append(".\n")
            .append(tabulation)
            .append(" *\n")
            .append(tabulation)
            .append(" * @since ")
            .append(this.version)
            .append('\n')
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
        builder.append("class ").append(this.name).append(" {\n");
        boolean flag = this.generateFields(builder, false, indent + 1);
        for (final Method method : this.methods) {
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(method.generate(indent + 1));
        }
        builder.append(tabulation).append("}\n");
        return builder.toString();
    }

    /**
     * Generated source code for fields.
     * @param builder String builder where to generate
     * @param separator Flg that indicated that need to add empty lune after previous entity
     * @param indent Indentation
     * @return New separator flag
     */
    private boolean generateFields(
        final StringBuilder builder,
        final boolean separator,
        final int indent
    ) {
        boolean flag = separator;
        for (final Field field : this.fields) {
            if (flag) {
                builder.append('\n');
            }
            flag = true;
            builder.append(field.generate(indent));
        }
        return flag;
    }
}
