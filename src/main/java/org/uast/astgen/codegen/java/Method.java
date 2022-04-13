/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.utils.StringUtils;

/**
 * Java method body.
 *
 * @since 1.0
 */
public final class Method implements Entity {
    /**
     * The descriptor.
     */
    private final MethodDescriptor descriptor;

    /**
     * The body.
     */
    private final MethodBody body;

    /**
     * The flag indicates that the method is public.
     */
    private boolean fpublic;

    /**
     * The flag indicates that the method is private.
     */
    private boolean fprivate;

    /**
     * The flag indicates that the method is abstract.
     */
    private boolean fabstract;

    /**
     * The flag indicates that the method is overridden.
     */
    private boolean foverride;

    /**
     * Main constructor.
     * @param brief The brief description.
     * @param name The name of the method.
     * @param foverride Flag indicates that the method is overridden
     */
    private Method(final String brief, final String name, final boolean foverride) {
        this.descriptor = new MethodDescriptor(brief, name);
        this.body = new MethodBody();
        this.fpublic = true;
        this.foverride = foverride;
    }

    /**
     * Constructor.
     * @param brief The brief description.
     * @param name The name of the method.
     */
    public Method(final String brief, final String name) {
        this(brief, name, false);
    }

    /**
     * Constructor (for overridden methods).
     * @param name The name of the method.
     */
    public Method(final String name) {
        this("", name, true);
    }

    /**
     * Adds the argument to the method.
     * @param type The type
     * @param name The name
     * @param description The brief description
     */
    public void addArgument(final String type, final String name, final String description) {
        this.descriptor.addArgument(type, name, description);
    }

    /**
     * Adds the argument to the method, without description.
     * @param type The type
     * @param name The name
     */
    public void addArgument(final String type, final String name) {
        this.descriptor.addArgument(type, name, "");
    }

    /**
     * Sets the return type.
     * @param type The type name
     * @param description The description what the method returns
     */
    public void setReturnType(final String type, final String description) {
        this.descriptor.setReturnType(type, description);
    }

    /**
     * Sets the return type (without description).
     * @param type The type name
     */
    public void setReturnType(final String type) {
        this.descriptor.setReturnType(type, "");
    }

    /**
     * Sets the new code.
     * @param str The new code
     */
    public void setCode(final String str) {
        this.body.setCode(str);
    }

    /**
     * Resets all flags.
     */
    public void resetFlags() {
        this.fpublic = false;
        this.fprivate = false;
        this.fabstract = false;
        this.foverride = false;
    }

    /**
     * Makes this method public.
     */
    public void makePublic() {
        this.fpublic = true;
        this.fprivate = false;
    }

    /**
     * Makes this method private.
     */
    public void makePrivate() {
        this.resetFlags();
        this.fprivate = true;
    }

    /**
     * Makes this method abstract.
     */
    public void makeAbstract() {
        this.resetFlags();
        this.fpublic = true;
        this.fabstract = true;
    }

    /**
     * Makes this method overridden.
     */
    public void makeOverridden() {
        this.resetFlags();
        this.fpublic = true;
        this.foverride = true;
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(64);
        if (this.foverride) {
            builder.append(tabulation).append("@Override\n");
        } else {
            builder.append(this.descriptor.generateHeader(indent));
        }
        builder.append(tabulation);
        if (this.fprivate) {
            builder.append("private ");
        } else if (this.fpublic) {
            builder.append("public ");
        }
        if (this.fabstract) {
            final String signature = this.descriptor.generateSignature(true);
            builder.append("abstract ").append(signature).append(";\n");
        } else {
            final String signature = this.descriptor.generateSignature(false);
            builder.append(signature).append(" {\n");
            final String code = this.body.generate(indent + 1);
            builder.append(code).append(tabulation).append("}\n");
        }
        return builder.toString();
    }
}
