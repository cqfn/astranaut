/*
 * MIT License Copyright (c) 2022 unified-ast
 * https://github.com/unified-ast/ast-generator/blob/master/LICENSE.txt
 */
package org.uast.astgen.codegen.java;

import org.uast.astgen.utils.StringUtils;

/**
 * Java constructor.
 *
 * @since 1.0
 */
public final class Constructor implements Entity {
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
     * Constructor.
     * @param name The name of the method.
     */
    public Constructor(final String name) {
        this.descriptor = new MethodDescriptor("Constructor", name).removeReturnType();
        this.body = new MethodBody();
        this.fpublic = true;
    }

    /**
     * Adds the argument to the descriptor.
     * @param type The type
     * @param name The name
     * @param description The brief description
     */
    public void addArgument(final String type, final String name, final String description) {
        this.descriptor.addArgument(type, name, description);
    }

    /**
     * Sets the new code.
     * @param str The new code
     */
    public void setCode(final String str) {
        this.body.setCode(str);
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
        this.fpublic = false;
        this.fprivate = true;
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder(64);
        builder.append(this.descriptor.generateHeader(indent)).append(tabulation);
        if (this.fprivate) {
            builder.append("private ");
        } else if (this.fpublic) {
            builder.append("public ");
        }
        final String signature = this.descriptor.generateSignature(false);
        builder.append(signature).append(" {\n");
        final String code = this.body.generate(indent + 1);
        builder.append(code).append(tabulation).append("}\n");
        return builder.toString();
    }
}
