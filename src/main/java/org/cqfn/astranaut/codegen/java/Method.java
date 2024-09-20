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

import org.cqfn.astranaut.utils.StringUtils;

/**
 * Java method body.
 *
 * @since 0.1.5
 */
public final class Method implements Entity {
    /**
     * The beginning of the method body.
     */
    private static final String BODY_BEGIN = " {\n";

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
     * The flag indicates that the method is static.
     */
    private boolean fstatic;

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
        this.fstatic = false;
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
        this.fstatic = false;
    }

    /**
     * Makes this method overridden.
     */
    public void makeOverridden() {
        this.resetFlags();
        this.fpublic = true;
        this.foverride = true;
        this.fstatic = false;
    }

    /**
     * Makes this method static.
     */
    public void makeStatic() {
        this.fabstract = false;
        this.foverride = false;
        this.fstatic = true;
    }

    /**
     * Returns the name of the method.
     * @return The name
     */
    public String getName() {
        return this.descriptor.getName();
    }

    @Override
    public String generate(final int indent) {
        final String tabulation = StringUtils.SPACE.repeat(indent * Entity.TAB_SIZE);
        final StringBuilder header = new StringBuilder(64);
        if (this.foverride) {
            header.append(tabulation).append("@Override\n");
        } else {
            header.append(this.descriptor.generateHeader(indent));
        }
        return header.toString().concat(this.generateCodeBlock(tabulation, indent));
    }

    /**
     * Generates code block, i.e. signature and body.
     * @param tabulation Calculated tabulation
     * @param indent Current indentation
     * @return Source code
     */
    private String generateCodeBlock(final String tabulation, final int indent) {
        StringBuilder block = new StringBuilder();
        block.append(tabulation);
        if (this.fprivate) {
            block.append("private ");
        } else if (this.fpublic) {
            block.append("public ");
        }
        if (this.fstatic) {
            block.append("static ");
        }
        if (this.fabstract) {
            final String signature = this.descriptor.generateSignature(true);
            block.append("abstract ").append(signature).append(";\n");
        } else {
            String signature = this.descriptor.generateSignature(false);
            final StringBuilder copy = new StringBuilder(block.toString());
            block.append(signature).append(Method.BODY_BEGIN);
            if (block.toString().length() >= Entity.MAX_LINE_LENGTH) {
                block = copy;
                final String offset = StringUtils.SPACE.repeat((indent + 1) * Entity.TAB_SIZE);
                signature = this.descriptor.generateLongSignature().replace("\t", offset);
                block.append(signature).append(Method.BODY_BEGIN);
            }
            final String code = this.body.generate(indent + 1);
            block.append(code).append(tabulation).append("}\n");
        }
        return block.toString();
    }
}
