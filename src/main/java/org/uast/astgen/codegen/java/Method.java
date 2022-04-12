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
     * Constructor.
     * @param name The name of the method.
     * @param brief The brief description.
     */
    public Method(final String name, final String brief) {
        this.descriptor = new MethodDescriptor(name, brief);
        this.body = new MethodBody();
        this.fpublic = true;
    }

    /**
     * Adds the argument to the descriptor.
     * @param type The type
     * @param name The name
     * @param description The descriptor
     */
    public void addArgument(final String type, final String name, final String description) {
        this.descriptor.addArgument(type, name, description);
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
            builder.append(this.descriptor.genHeader(indent));
        }
        builder.append(tabulation);
        if (this.fprivate) {
            builder.append("private ");
        } else if (this.fpublic) {
            builder.append("public ");
        }
        if (this.fabstract) {
            builder.append("abstract ");
        }
        if (this.fabstract) {
            final String signature = this.descriptor.genSignature(true);
            builder.append(signature).append(";\n");
        } else {
            final String signature = this.descriptor.genSignature(false);
            builder.append(signature).append(" {\n");
            final String code = this.body.generate(indent + 1);
            builder.append(code).append(tabulation).append("}\n");
        }
        return builder.toString();
    }
}
