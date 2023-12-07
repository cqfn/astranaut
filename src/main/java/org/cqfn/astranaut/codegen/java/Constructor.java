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

import org.cqfn.astranaut.utils.StringUtils;

/**
 * Java constructor.
 *
 * @since 0.1.5
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
