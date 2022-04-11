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
     * The flag indicates that the method is not public.
     */
    private boolean nonpublic;

    /**
     * Constructor.
     * @param name The name of the method.
     * @param brief The brief description.
     */
    public Method(final String name, final String brief) {
        this.descriptor = new MethodDescriptor(name, brief);
        this.body = new MethodBody();
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
     * Makes this method public.
     */
    public void makePublic() {
        this.nonpublic = false;
    }

    /**
     * Makes this method public.
     */
    public void makePrivate() {
        this.nonpublic = true;
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder builder = new StringBuilder();
        builder.append(this.descriptor.genHeader(indent)).append(tabulation);
        if (this.nonpublic) {
            builder.append("private ");
        } else {
            builder.append("public ");
        }
        final String signature = this.descriptor.genSignature(false);
        builder.append(signature).append(" {\n");
        final String code = this.body.generate(indent + 1);
        builder.append(code).append(tabulation).append("}\n");
        return builder.toString();
    }
}
